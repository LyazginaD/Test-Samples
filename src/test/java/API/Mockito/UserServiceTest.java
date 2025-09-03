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
        // Тест создания пользователя
        User userToSave = new User(null, "John Doe", "john@example.com");
        User savedUser = new User(1L, "John Doe", "john@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser("John Doe", "john@example.com");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    @Feature("Registration")
    void testCreateUser_InvalidName_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("", "test@example.com");
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @Feature("API")
    void testGetUserById_UserExists() {
        // Arrange
        User user = new User(1L, "John Doe", "john@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_UserNotExists() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(
                new User(1L, "John Doe", "john@example.com"),
                new User(2L, "Jane Smith", "jane@example.com")
        );
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testDeleteUser_UserExists() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        boolean result = userService.deleteUser(1L);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserNotExists() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = userService.deleteUser(999L);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateUserEmail_Success() {
        // Arrange
        User existingUser = new User(1L, "John Doe", "old@example.com");
        User updatedUser = new User(1L, "John Doe", "new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUserEmail(1L, "new@example.com");

        // Assert
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserEmail_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserEmail(999L, "new@example.com");
        });

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

}