package UnitTests;

import com.example.usermanagement.event.EventBus;
import com.example.usermanagement.event.EventListener;
import com.example.usermanagement.event.EventType;
import com.example.usermanagement.event.UserEvent;
import com.example.usermanagement.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EventBus - Unit тесты")
class EventBusTest {

    private EventBus eventBus;
    private User testUser;

    @BeforeEach
    void setUp() {
        eventBus = new EventBus();
        testUser = new User(1L, "Test User", "test@example.com");
    }

    @Test
    @DisplayName("Подписка и публикация события")
    void testSubscribeAndPublish() {
        // Arrange
        AtomicInteger eventCount = new AtomicInteger(0);
        EventListener listener = event -> eventCount.incrementAndGet();

        // Act
        eventBus.subscribe(listener);
        eventBus.publish(new UserEvent(EventType.USER_CREATED, testUser, "Test"));

        // Assert
        assertEquals(1, eventCount.get(), "Событие должно быть доставлено подписчику");
    }

    @Test
    @DisplayName("Отписка от событий")
    void testUnsubscribe() {
        // Arrange
        AtomicInteger eventCount = new AtomicInteger(0);
        EventListener listener = event -> eventCount.incrementAndGet();

        // Act
        eventBus.subscribe(listener);
        eventBus.unsubscribe(listener);
        eventBus.publish(new UserEvent(EventType.USER_CREATED, testUser, "Test"));

        // Assert
        assertEquals(0, eventCount.get(), "Отписанный слушатель не должен получать события");
    }

    @Test
    @DisplayName("Множественные подписчики")
    void testMultipleSubscribers() {
        // Arrange
        AtomicInteger count1 = new AtomicInteger(0);
        AtomicInteger count2 = new AtomicInteger(0);

        EventListener listener1 = event -> count1.incrementAndGet();
        EventListener listener2 = event -> count2.incrementAndGet();

        // Act
        eventBus.subscribe(listener1);
        eventBus.subscribe(listener2);
        eventBus.publish(new UserEvent(EventType.USER_CREATED, testUser, "Test"));

        // Assert
        assertAll("Оба подписчика должны получить событие",
                () -> assertEquals(1, count1.get()),
                () -> assertEquals(1, count2.get())
        );
    }

    @ParameterizedTest
    @EnumSource(EventType.class)
    @DisplayName("Публикация всех типов событий")
    void testPublishAllEventTypes(EventType eventType) {
        // Arrange
        AtomicInteger eventCount = new AtomicInteger(0);
        EventListener listener = event -> eventCount.incrementAndGet();

        // Act
        eventBus.subscribe(listener);
        eventBus.publish(new UserEvent(eventType, testUser, "Test"));

        // Assert
        assertEquals(1, eventCount.get(), "Событие типа " + eventType + " должно быть доставлено");
    }

    @Test
    @DisplayName("Обработка исключений в подписчиках")
    void testExceptionHandlingInSubscribers() {
        // Arrange
        AtomicInteger workingListenerCount = new AtomicInteger(0);

        EventListener failingListener = event -> {
            throw new RuntimeException("Test exception");
        };

        EventListener workingListener = event -> workingListenerCount.incrementAndGet();

        // Act
        eventBus.subscribe(failingListener);
        eventBus.subscribe(workingListener);

        // Не должно бросать исключение
        assertDoesNotThrow(() -> eventBus.publish(new UserEvent(EventType.USER_CREATED, testUser, "Test")));

        // Assert
        assertEquals(1, workingListenerCount.get(),
                "Работающий подписчик должен получить событие несмотря на ошибку в другом");
    }

    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    @DisplayName("Публикация событий не должна блокировать надолго")
    void testPublishPerformance() {
        // Arrange
        for (int i = 0; i < 1000; i++) {
            eventBus.subscribe(event -> {});
        }

        // Act & Assert - не должно занимать больше 100ms
        assertDoesNotThrow(() -> eventBus.publish(new UserEvent(EventType.USER_CREATED, testUser, "Test")));
    }

    @Test
    @DisplayName("Подсчет количества подписчиков")
    void testGetSubscribersCount() {
        // Arrange
        EventListener listener1 = event -> {};
        EventListener listener2 = event -> {};

        // Act & Assert
        assertEquals(0, eventBus.getSubscribersCount(), "Изначально подписчиков нет");

        eventBus.subscribe(listener1);
        assertEquals(1, eventBus.getSubscribersCount(), "После подписки одного слушателя");

        eventBus.subscribe(listener2);
        assertEquals(2, eventBus.getSubscribersCount(), "После подписки двух слушателей");

        eventBus.unsubscribe(listener1);
        assertEquals(1, eventBus.getSubscribersCount(), "После отписки одного слушателя");
    }

    @Test
    @DisplayName("Публикация null события")
    void testPublishNullEvent() {
        // Act & Assert - не должно бросать исключение
        assertDoesNotThrow(() -> eventBus.publish(null));
    }

    @Test
    @DisplayName("Подписка null слушателя")
    void testSubscribeNullListener() {
        // Act & Assert - не должно бросать исключение
        assertDoesNotThrow(() -> eventBus.subscribe(null));
    }

    @Test
    @DisplayName("Отписка null слушателя")
    void testUnsubscribeNullListener() {
        // Act & Assert - не должно бросать исключение
        assertDoesNotThrow(() -> eventBus.unsubscribe(null));
    }

    @Test
    @DisplayName("Отписка не подписанного слушателя")
    void testUnsubscribeNotSubscribed() {
        // Arrange
        EventListener listener = event -> {};

        // Act & Assert - не должно бросать исключение
        assertDoesNotThrow(() -> eventBus.unsubscribe(listener));
    }
}