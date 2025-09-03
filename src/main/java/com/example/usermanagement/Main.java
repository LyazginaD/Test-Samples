package com.example.usermanagement;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.impl.InMemoryUserRepository;
import com.example.usermanagement.service.UserService;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService(new InMemoryUserRepository());

        // Создаем пользователей
        User user1 = userService.createUser("John Doe", "john@example.com");
        User user2 = userService.createUser("Jane Smith", "jane@example.com");

        System.out.println("Created users:");
        userService.getAllUsers().forEach(System.out::println);

        // Обновляем email
        User updatedUser = userService.updateUserEmail(user1.getId(), "john.new@example.com");
        System.out.println("Updated user: " + updatedUser);

        // Удаляем пользователя
        boolean deleted = userService.deleteUser(user2.getId());
        System.out.println("User deleted: " + deleted);

        System.out.println("Remaining users:");
        userService.getAllUsers().forEach(System.out::println);
    }
}