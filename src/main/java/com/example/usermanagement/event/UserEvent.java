package com.example.usermanagement.event;

import com.example.usermanagement.model.User;

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
}