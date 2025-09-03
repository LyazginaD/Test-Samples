package com.example.usermanagement.repository;

import com.example.usermanagement.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}