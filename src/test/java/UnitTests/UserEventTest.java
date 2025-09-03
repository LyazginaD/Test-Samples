package UnitTests;

import com.example.usermanagement.event.EventType;
import com.example.usermanagement.event.UserEvent;
import com.example.usermanagement.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserEvent - Unit тесты")
class UserEventTest {

    @Test
    @DisplayName("Равенство событий")
    void testUserEventEquality() {
        // Arrange
        User user = new User(1L, "Test User", "test@example.com");
        UserEvent event1 = new UserEvent(EventType.USER_CREATED, user, "Message");
        UserEvent event2 = new UserEvent(EventType.USER_CREATED, user, "Message");
        UserEvent event3 = new UserEvent(EventType.USER_DELETED, user, "Different");

        // Assert
        assertEquals(event1, event2, "Одинаковые события должны быть равны");
        assertNotEquals(event1, event3, "Разные события не должны быть равны");
        assertNotEquals(null, event1, "Объект не должен быть равен null");
        assertNotEquals("not an event", event1, "Объект не должен быть равен объекту другого типа");
    }

    @Test
    @DisplayName("HashCode consistency")
    void testUserEventHashCode() {
        // Arrange
        User user = new User(1L, "Test User", "test@example.com");
        UserEvent event1 = new UserEvent(EventType.USER_CREATED, user, "Message");
        UserEvent event2 = new UserEvent(EventType.USER_CREATED, user, "Message");

        // Assert
        assertEquals(event1.hashCode(), event2.hashCode(),
                "HashCode должен быть одинаковым для одинаковых событий");
    }
}