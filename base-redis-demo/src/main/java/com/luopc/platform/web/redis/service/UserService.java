package com.luopc.platform.web.redis.service;


import com.luopc.platform.web.redis.model.User;
import com.luopc.platform.web.redis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByAge(Integer age) {
        return userRepository.findByAge(age);
    }

    public List<User> findByAgeBetween(Integer minAge, Integer maxAge) {
        return userRepository.findByAgeBetween(minAge, maxAge);
    }

    public List<User> findByAgeGreaterThan(Integer age) {
        return userRepository.findByAgeGreaterThan(age);
    }

    public List<User> findByNameContaining(String name) {
        return userRepository.findByNameContaining(name);
    }

    public long count() {
        return userRepository.count();
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }
}
