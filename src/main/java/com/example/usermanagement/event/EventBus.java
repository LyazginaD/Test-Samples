package com.example.usermanagement.event;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBus {
    private final List<EventListener> listeners = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(EventBus.class.getName());

    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(EventListener listener) {
        listeners.remove(listener);
    }

    public void publish(UserEvent event) {
        for (EventListener listener : listeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                // Логируем ошибку, но продолжаем обработку для других подписчиков
                logger.log(Level.WARNING, "Error handling event from subscriber: " +
                        listener.getClass().getSimpleName() + ", Event: " + event.getType(), e);
            }
        }
    }

    public int getSubscribersCount() {
        return listeners.size();
    }

    public List<EventListener> getSubscribers() {
        return new ArrayList<>(listeners);
    }
}