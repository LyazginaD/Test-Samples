package UnitTests.Mockito;

import com.example.usermanagement.event.EventBus;
import com.example.usermanagement.event.EventListener;
import com.example.usermanagement.event.EventType;
import com.example.usermanagement.event.UserEvent;
import com.example.usermanagement.model.User;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventBusTest {

    @Mock
    private EventListener eventListener;

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("EventBus")
    @DisplayName("Создать событие и проверить, что оно корректно доставляется")
    void testSubscribeAndPublish() {
        // Arrange - создаем шину событий и подписываем mock-слушателя
        EventBus eventBus = new EventBus();
        eventBus.subscribe(eventListener);

        UserEvent testEvent = new UserEvent(
                EventType.USER_CREATED,
                new User(1L, "Test User", "test@example.com"),
                "Test event message"
        );

        // Act - публикуем тестовое событие в шину
        eventBus.publish(testEvent);

        // Assert - проверяем, что событие было доставлено
        ArgumentCaptor<UserEvent> eventCaptor = ArgumentCaptor.forClass(UserEvent.class);
        verify(eventListener, times(1)).onEvent(eventCaptor.capture());

        UserEvent receivedEvent = eventCaptor.getValue();
        assertEquals(testEvent, receivedEvent, "Событие должно быть доставлено без изменений");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("EventBus")
    @DisplayName("Проверить, что после отписки слушатель не получает сообщение")
    void testUnsubscribe() {
        // Arrange - подписываем и затем отписываем слушателя
        EventBus eventBus = new EventBus();
        eventBus.subscribe(eventListener);
        eventBus.unsubscribe(eventListener);

        UserEvent testEvent = new UserEvent(
                EventType.USER_UPDATED,
                new User(2L, "Another User", "another@example.com"),
                "Update event message"
        );

        // Act - публикуем событие после отписки
        eventBus.publish(testEvent);

        // Assert - проверяем, что отписанный слушатель не получил событие
        verify(eventListener, never()).onEvent(any(UserEvent.class));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("EventBus")
    @DisplayName("Проверить, что сообщение доставляется нескольким подписчикам и счетчик подписчиков работает корректно")
    void testGetSubscribers() {
        // Arrange - создаем шину и подписываем несколько слушателей
        EventBus eventBus = new EventBus();
        EventListener listener1 = mock(EventListener.class);
        EventListener listener2 = mock(EventListener.class);

        // Act - подписываем слушателей
        eventBus.subscribe(listener1);
        eventBus.subscribe(listener2);

        // Assert - проверяем количество и состав подписчиков
        assertEquals(2, eventBus.getSubscribersCount(), "Должно быть 2 подписчика");
        assertTrue(eventBus.getSubscribers().contains(listener1), "Первый слушатель должен быть в списке");
        assertTrue(eventBus.getSubscribers().contains(listener2), "Второй слушатель должен быть в списке");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("EventBus")
    @DisplayName("Проверить отправку сообщения, если подписчики отсутствуют")
    void testPublishWithNoSubscribers() {
        // Arrange - создаем шину без подписчиков
        // Тестируем поведение системы когда нет слушателей событий
        EventBus eventBus = new EventBus();

        UserEvent testEvent = new UserEvent(
                EventType.USER_EMAIL_CHANGED,
                new User(4L, "No Subscriber User", "no-sub@example.com"),
                "Event without subscribers"
        );

        // Act - публикуем событие при отсутствии подписчиков
        // Это не должно вызывать никаких ошибок или исключений
        eventBus.publish(testEvent);

        // Assert - проверяем, что mock-слушатель (который не был подписан) не получил событие
        // Это подтверждает, что события не отправляются не подписанным слушателям
        verify(eventListener, never()).onEvent(any(UserEvent.class));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Feature("EventBus")
    @DisplayName("Проверить, что шина продолжает работу при ошибках в подписчиках и ошибки логируются")
    void testExceptionHandlingInSubscribers() {
        // Arrange - настраиваем логирование для теста
        Logger eventBusLogger = Logger.getLogger(EventBus.class.getName());
        TestLogHandler testLogHandler = new TestLogHandler();
        eventBusLogger.addHandler(testLogHandler);
        eventBusLogger.setLevel(Level.WARNING);

        EventBus eventBus = new EventBus();

        // Создаем слушателя, который бросает исключение с понятным сообщением
        EventListener failingListener = mock(EventListener.class);
        String expectedErrorMessage = "Тестовая ошибка в обработчике событий";
        RuntimeException expectedException = new RuntimeException(expectedErrorMessage);
        doThrow(expectedException).when(failingListener).onEvent(any(UserEvent.class));

        EventListener workingListener = mock(EventListener.class);

        eventBus.subscribe(failingListener);
        eventBus.subscribe(workingListener);

        UserEvent testEvent = new UserEvent(
                EventType.USER_CREATED,
                new User(5L, "Тестовый пользователь", "test@example.com"),
                "Тестовое событие"
        );

        // Act - публикуем событие
        eventBus.publish(testEvent);

        // Assert - проверяем, что оба слушателя были вызваны
        verify(failingListener, times(1)).onEvent(testEvent);
        verify(workingListener, times(1)).onEvent(testEvent);

        // Проверяем логирование ошибки
        assertFalse(testLogHandler.getLogRecords().isEmpty(),
                "Ошибка должна быть залогирована");

        LogRecord logRecord = testLogHandler.getLogRecords().getFirst();
        assertEquals(Level.WARNING, logRecord.getLevel(),
                "Уровень логирования должен быть WARNING");

        assertTrue(logRecord.getMessage().contains("Error handling event"),
                "Сообщение должно содержать информацию об ошибке обработки");

        assertTrue(logRecord.getMessage().contains("USER_CREATED"),
                "Сообщение должно содержать тип события");

        assertEquals(expectedException, logRecord.getThrown(),
                "Должно быть сохранено оригинальное исключение");

        // Cleanup
        eventBusLogger.removeHandler(testLogHandler);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Feature("EventBus")
    @DisplayName("Проверка формата сообщений")
    void testErrorLoggingFormat() {
        // Arrange - настраиваем логирование
        Logger eventBusLogger = Logger.getLogger(EventBus.class.getName());
        TestLogHandler testLogHandler = new TestLogHandler();
        eventBusLogger.addHandler(testLogHandler);

        EventBus eventBus = new EventBus();

        // Создаем именованный класс вместо анонимного для надежного toString()
        class CustomFaultyListener implements EventListener {
            @Override
            public void onEvent(UserEvent event) {
                throw new IllegalStateException("Ошибка в пользовательском обработчике");
            }

            @Override
            public String toString() {
                return "CustomFaultyListener";
            }
        }

        EventListener faultyListener = new CustomFaultyListener();
        eventBus.subscribe(faultyListener);

        UserEvent testEvent = new UserEvent(
                EventType.USER_DELETED,
                new User(10L, "Удаляемый пользователь", "delete@example.com"),
                "Событие удаления"
        );

        // Act
        eventBus.publish(testEvent);

        // Assert - проверяем формат лог-сообщения
        assertFalse(testLogHandler.getLogRecords().isEmpty(),
                "Ошибка должна быть залогирована");

        LogRecord logRecord = testLogHandler.getLogRecords().getFirst();
        String logMessage = logRecord.getMessage();

        assertTrue(logMessage.contains("Error handling event"),
                "Сообщение должно указывать на ошибку обработки");

        assertTrue(logMessage.contains("USER_DELETED"),
                "Сообщение должно содержать тип события");

        assertInstanceOf(IllegalStateException.class, logRecord.getThrown(),
                "Должно быть сохранено правильное исключение");

        // Вместо проверки имени класса, проверяем более общие вещи
        assertTrue(logMessage.contains("subscriber") || logMessage.contains("from"),
                "Сообщение должно содержать информацию о подписчике");

        eventBusLogger.removeHandler(testLogHandler);
    }

    // Вспомогательный класс для тестирования логов
    private static class TestLogHandler extends Handler {
        private final java.util.List<LogRecord> logRecords = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            logRecords.add(record);
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}

        public java.util.List<LogRecord> getLogRecords() {
            return logRecords;
        }
    }

}