package com.example.usermanagement;

import com.example.usermanagement.event.*;
import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.impl.InMemoryUserRepository;
import com.example.usermanagement.service.UserService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        // Настраиваем подробное логирование для демонстрации
        Logger.getLogger("com.example.usermanagement").setLevel(Level.ALL);

        System.out.println("=== Демонстрация работы шины событий с обработкой ошибок ===\n");

        EventBus eventBus = new EventBus();

        // Добавляем разные типы обработчиков
        eventBus.subscribe(new EventListener() {
            @Override
            public void onEvent(UserEvent event) {
                System.out.println("📋 Логгер: " + event.getType() + " - " + event.getUser().getName());
            }

            @Override
            public String toString() {
                return "ConsoleLogger";
            }
        });

        eventBus.subscribe(new EventListener() {
            @Override
            public void onEvent(UserEvent event) {
                // Этот обработчик будет падать с ошибкой
                throw new RuntimeException("Ошибка в обработчике уведомлений!");
            }

            @Override
            public String toString() {
                return "NotificationHandler";
            }
        });

        eventBus.subscribe(new EventListener() {
            @Override
            public void onEvent(UserEvent event) {
                System.out.println("✅ Аудитор: Зафиксировано событие " + event.getType());
            }

            @Override
            public String toString() {
                return "AuditTrail";
            }
        });

        UserService userService = new UserService(new InMemoryUserRepository(), eventBus);

        try {
            System.out.println("Создаем пользователя...");
            User user = userService.createUser("Иван Иванов", "ivan@example.com");

            System.out.println("\nОбновляем email...");
            userService.updateUserEmail(user.getId(), "ivan.new@example.com");

            System.out.println("\nУдаляем пользователя...");
            userService.deleteUser(user.getId());

        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
        }

        System.out.println("\n=== Демонстрация завершена ===");
        System.out.println("Обратите внимание, что ошибки в обработчиках были перехвачены");
        System.out.println("и система продолжила работу нормально!");
    }
}