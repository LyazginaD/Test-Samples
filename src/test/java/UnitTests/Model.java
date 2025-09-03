package UnitTests;

import com.example.usermanagement.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Модель User - Unit тесты")
class UserTest {

    @Test
    @DisplayName("Создание пользователя с валидными данными")
    void testUserCreation_ValidData() {
        // Arrange & Act
        User user = new User(1L, "John Doe", "john@example.com");

        // Assert
        assertAll("Проверка всех полей пользователя",
                () -> assertEquals(1L, user.getId()),
                () -> assertEquals("John Doe", user.getName()),
                () -> assertEquals("john@example.com", user.getEmail())
        );
    }

    @Test
    @DisplayName("Равенство пользователей по ID")
    void testUserEquality() {
        // Arrange
        User user1 = new User(1L, "John Doe", "john@example.com");
        User user2 = new User(1L, "Different Name", "different@example.com");
        User user3 = new User(2L, "John Doe", "john@example.com");

        // Assert
        assertEquals(user1, user2, "Пользователи с одинаковым ID должны быть равны");
        assertNotEquals(user1, user3, "Пользователи с разным ID не должны быть равны");
        assertNotEquals(null, user1, "Объект не должен быть равен null");
        assertNotEquals("not a user", user1, "Объект не должен быть равен объекту другого типа");
    }

    @Test
    @DisplayName("HashCode consistency")
    void testUserHashCode() {
        // Arrange
        User user1 = new User(1L, "John Doe", "john@example.com");
        User user2 = new User(1L, "John Doe", "john@example.com");

        // Assert
        assertEquals(user1.hashCode(), user2.hashCode(),
                "HashCode должен быть одинаковым для объектов с одинаковым ID");
    }
}