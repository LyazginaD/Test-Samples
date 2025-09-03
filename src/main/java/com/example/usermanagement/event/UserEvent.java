package com.example.usermanagement.event;

import com.example.usermanagement.model.User;

import java.util.Objects;

public class UserEvent {
    private final EventType type;
    private final User user;
    private final String message;

    public UserEvent(EventType type, User user, String message) {
        this.type = type;
        this.user = user;
        this.message = message;
    }

    public EventType getType() { return type; }
    public User getUser() { return user; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "UserEvent{type=" + type + ", user=" + user + ", message='" + message + "'}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEvent userEvent = (UserEvent) o;
        return type == userEvent.type &&
                Objects.equals(user, userEvent.user) &&
                Objects.equals(message, userEvent.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, user, message);
    }
}