package com.example.usermanagement.service;

import com.example.usermanagement.event.EventBus;
import com.example.usermanagement.event.EventType;
import com.example.usermanagement.event.UserEvent;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    private final EventBus eventBus;

    public UserService(UserRepository userRepository, EventBus eventBus) {
        this.userRepository = userRepository;
        this.eventBus = eventBus;
    }

    public User createUser(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        User user = new User(null, name, email);
        User savedUser = userRepository.save(user);

        // Публикуем событие о создании пользователя
        eventBus.publish(new UserEvent(
                EventType.USER_CREATED,
                savedUser,
                "User created successfully"
        ));

        return savedUser;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean deleteUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            userRepository.deleteById(id);

            // Публикуем событие об удалении пользователя
            eventBus.publish(new UserEvent(
                    EventType.USER_DELETED,
                    userOpt.get(),
                    "User deleted successfully"
            ));

            return true;
        }
        return false;
    }

    public User updateUserEmail(Long id, String newEmail) {
        if (newEmail == null || !newEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String oldEmail = user.getEmail();
            user.setEmail(newEmail);
            User updatedUser = userRepository.save(user);

            // Публикуем событие об изменении email
            eventBus.publish(new UserEvent(
                    EventType.USER_EMAIL_CHANGED,
                    updatedUser,
                    "Email changed from " + oldEmail + " to " + newEmail
            ));

            return updatedUser;
        }
        throw new IllegalArgumentException("User not found with id: " + id);
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}