package UnitTests.Mockito;

import com.example.usermanagement.event.EventBus;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.UserService;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventBus eventBus; // ← ДОБАВЛЯЕМ MOCK ДЛЯ EVENTBUS

    @InjectMocks
    private UserService userService;

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Registration")
    @DisplayName("Создать пользователя и проверить, что пользователь существует")
    void testCreateUser_Success() {
        // Arrange - подготавливаем тестовые данные: валидное имя и валидный email
        String testName = "John Doe";
        String testEmail = "john@example.com";

        // Настраиваем mock репозитория, чтобы когда сохранят любого пользователя, вернуть его же с ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            return new User(1L, userToSave.getName(), userToSave.getEmail());
        });

        // Act
        User result = userService.createUser(testName, testEmail);

        // Assert - проверяем, что пользователь создан и его данные соответствуют тестовым
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

        // Verify - проверяем, что событие было опубликовано
        verify(eventBus, times(1)).publish(any());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("User Service")
    @DisplayName("Создать пользователя - неверное имя пользователя")
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
        verify(eventBus, never()).publish(any()); // И событие не публикуется
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("User Service")
    @DisplayName("Получить данные пользователя - пользователь существует")
    void testGetUserById_UserExists() {
        // Arrange - создаем тестового пользователя
        User user = new User(1L, "John Doe", "john@example.com");

        // Настраиваем mock репозитория, чтобы он возвращал этого пользователя при поиске по ID
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act - вызываем метод сервиса для поиска пользователя по ID
        Optional<User> result = userService.getUserById(1L);

        // Assert - проверяем, что пользователь найден и его данные корректны
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());

        // Verify - убеждаемся, что метод findById был вызван ровно один раз
        // с правильным идентификатором
        verify(userRepository, times(1)).findById(1L);
        // Для методов чтения события не публикуются
        verify(eventBus, never()).publish(any());
    }

    // ... остальные методы чтения (getAllUsers, getUserById_UserNotExists)
    // тоже не должны публиковать события

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("User Service")
    @DisplayName("Обновить пользователя - пользователь существует")
    void testUpdateUserEmail_Success() {
        // Arrange - создаем существующего пользователя и ожидаемого обновленного пользователя
        User existingUser = new User(1L, "John Doe", "old@example.com");
        User updatedUser = new User(1L, "John Doe", "new@example.com");

        // Настраиваем mock репозитория для поиска и сохранения
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act - вызываем метод сервиса для обновления email пользователя
        User result = userService.updateUserEmail(1L, "new@example.com");

        // Assert - проверяем, что email был успешно обновлен
        assertEquals("new@example.com", result.getEmail());

        // Verify - убеждаемся, что методы findById и save были вызваны по одному разу
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));

        // Verify - проверяем, что событие было опубликовано
        verify(eventBus, times(1)).publish(any());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("User Service")
    @DisplayName("Удалить пользователя - пользователь существует")
    void testDeleteUser_UserExists() {
        // Arrange - настраиваем mock репозитория: пользователь существует
        User existingUser = new User(1L, "John Doe", "john@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).deleteById(1L);

        // Act - вызываем метод сервиса для удаления существующего пользователя
        boolean result = userService.deleteUser(1L);

        // Assert - проверяем, что удаление прошло успешно
        assertTrue(result);

        // Verify - убеждаемся, что методы были вызваны
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);

        // Verify - проверяем, что событие было опубликовано
        verify(eventBus, times(1)).publish(any());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("User Service")
    @DisplayName("Удалить пользователя - пользователь отсутствует")
    void testDeleteUser_UserNotExists() {
        // Arrange - настраиваем mock репозитория: пользователь не существует
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act - вызываем метод сервиса для удаления несуществующего пользователя
        boolean result = userService.deleteUser(999L);

        // Assert - проверяем, что удаление не выполнено (возвращено false)
        assertFalse(result);

        // Verify - убеждаемся, что findById был вызван, а deleteById - никогда
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).deleteById(anyLong());

        // Verify - и событие не публиковалось
        verify(eventBus, never()).publish(any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalid", "no-at-sign", "missing@domain"})
    @Severity(SeverityLevel.NORMAL)
    @Feature("Validation")
    @DisplayName("Создание пользователя с невалидным email")
    void testCreateUser_InvalidEmail(String invalidEmail) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser("Valid Name", invalidEmail));

        assertEquals("Invalid email format", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Feature("Validation")
    @DisplayName("Обновление email с невалидным адресом")
    void testUpdateUserEmail_InvalidEmail() {
        // Act & Assert - валидация должна происходить ДО поиска пользователя
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserEmail(1L, "invalid-email"));

        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
        verify(eventBus, never()).publish(any());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Feature("Edge Cases")
    @DisplayName("Обновление email на тот же самый email")
    void testUpdateUserEmail_SameEmail() {
        // Arrange
        String sameEmail = "same@example.com";
        User existingUser = new User(1L, "John Doe", sameEmail);
        User updatedUser = new User(1L, "John Doe", sameEmail);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUserEmail(1L, sameEmail);

        // Assert
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(sameEmail, result.getEmail());
        verify(userRepository, times(1)).save(any());
        verify(eventBus, times(1)).publish(any());
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Feature("Edge Cases")
    @DisplayName("Попытка удаления пользователя с null ID")
    void testDeleteUser_NullId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(null));

        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).deleteById(any());
        verify(eventBus, never()).publish(any());
    }
}