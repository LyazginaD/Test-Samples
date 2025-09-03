package API.Mockito;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.UserService;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @Feature("Registration")
    void testCreateUser_Success() {
        // Arrange - подготавливаем тестовые данные: валидное имя и валидный email
        String testName = "John Doe";
        String testEmail = "john@example.com";

        //Настраиваем mock репозитория, чтобы когда сохранят любого пользователя, вернуть его же с ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            return new User(1L, userToSave.getName(), userToSave.getEmail());
        });

        // Act
        User result = userService.createUser(testName, testEmail);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testName, result.getName());
        assertEquals(testEmail, result.getEmail());

        // Проверяем, что save был вызван ровно один раз
        verify(userRepository, times(1)).save(any(User.class));

        // Дополнительная проверка: убеждаемся, что переданный в save пользователь
        // имел правильные данные (кроме ID)
        verify(userRepository).save(argThat(user ->
                user.getName().equals(testName) &&
                        user.getEmail().equals(testEmail) &&
                        user.getId() == null
        ));
    }

    @Test
    @Feature("User Service")
    void testCreateUser_InvalidName() {
        // Arrange - подготавливаем тестовые данные: пустое имя и валидный email
        String testName = "";
        String testEmail = "test@example.com";

        // Act & Assert - проверяем, что при пустом имени выбрасывается исключение
        // и метод save репозитория не вызывается
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(testName, testEmail));

        // Verify - убеждаемся, что операция сохранения не была вызвана
        // при невалидных данных, что предотвращает сохранение некорректных данных
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @Feature("User Service")
    void testGetUserById_UserExists() {
        // Arrange - создаем тестового пользователя
        User user = new User(1L, "John Doe", "john@example.com");

        //Настраиваем mock репозитория, чтобы он возвращал этого пользователя при поиске по ID
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act - вызываем метод сервиса для поиска пользователя по ID
        Optional<User> result = userService.getUserById(1L);

        // Assert - проверяем, что пользователь найден и его данные корректны
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());

        // Verify - убеждаемся, что метод findById был вызван ровно один раз
        // с правильным идентификатором
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @Feature("User Service")
    void testGetUserById_UserNotExists() {
        // Arrange - настраиваем mock репозитория чтобы он возвращал empty Optional
        // при поиске несуществующего пользователя (ID 999L)
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act - вызываем метод сервиса для поиска несуществующего пользователя
        Optional<User> result = userService.getUserById(999L);

        // Assert - проверяем, что пользователь действительно не найден
        assertFalse(result.isPresent());

        // Verify - убеждаемся, что метод findById был вызван ровно один раз
        // с указанным идентификатором
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @Feature("User Service")
    void testGetAllUsers() {
        // Arrange - создаем список тестовых пользователей
        List<User> users = Arrays.asList(
                new User(1L, "John Doe", "john@example.com"),
                new User(2L, "Jane Smith", "jane@example.com")
        );
        //Настраиваем mock репозитория, чтобы он возвращал этот список при вызове findAll
        when(userRepository.findAll()).thenReturn(users);

        // Act - вызываем метод сервиса для получения всех пользователей
        List<User> result = userService.getAllUsers();

        // Assert - проверяем, что возвращен правильный количество пользователей
        assertEquals(2, result.size());

        // Verify - убеждаемся, что метод findAll был вызван ровно один раз
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @Feature("User Service")
    void testDeleteUser_UserExists() {
        // Arrange - настраиваем mock репозитория: пользователь существует
        // и метод deleteById не делает ничего (void метод)
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act - вызываем метод сервиса для удаления существующего пользователя
        boolean result = userService.deleteUser(1L);

        // Assert - проверяем, что удаление прошло успешно
        assertTrue(result);

        // Verify - убеждаемся, что методы existsById и deleteById были вызваны
        // по одному разу с правильными параметрами
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @Feature("User Service")
    void testDeleteUser_UserNotExists() {
        // Arrange - настраиваем mock репозитория: пользователь не существует
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act - вызываем метод сервиса для удаления несуществующего пользователя
        boolean result = userService.deleteUser(999L);

        // Assert - проверяем, что удаление не выполнено (возвращено false)
        assertFalse(result);

        // Verify - убеждаемся, что existsById был вызван, а deleteById - никогда
        // это предотвращает попытки удаления несуществующих записей
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @Feature("User Service")
    void testUpdateUserEmail_Success() {
        // Arrange - создаем существующего пользователя и ожидаемого обновленного пользователя
        User existingUser = new User(1L, "John Doe", "old@example.com");
        User updatedUser = new User(1L, "John Doe", "new@example.com");

        //Настраиваем mock репозитория для поиска и сохранения
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act - вызываем метод сервиса для обновления email пользователя
        User result = userService.updateUserEmail(1L, "new@example.com");

        // Assert - проверяем, что email был успешно обновлен
        assertEquals("new@example.com", result.getEmail());

        // Verify - убеждаемся, что методы findById и save были вызваны по одному разу
        // это гарантирует корректную последовательность операций
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @Feature("User Service")
    void testUpdateUserEmail_UserNotFound() {
        // Arrange - настраиваем mock репозитория: пользователь не найден
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert - проверяем, что при попытке обновления несуществующего пользователя
        // выбрасывается исключение IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserEmail(999L, "new@example.com"));

        // Verify - убеждаемся, что findById был вызван, а save - никогда
        // это предотвращает создание новых записей при ошибке обновления
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }
}