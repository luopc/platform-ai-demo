package com.luopc.platform.web.mongodb.service;


import com.luopc.platform.web.mongodb.model.User;
import com.luopc.platform.web.mongodb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        user.setUpdateTime(LocalDateTime.now());
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

    public List<User> findByNameOrEmail(String name, String email) {
        return userRepository.findByNameOrEmail(name, email);
    }

    public Page<User> findPage(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepository.findAll(pageable);
    }

    public Page<User> findPageWithCondition(String name, Integer minAge, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByNameAndAgeGreaterThan(name, minAge, pageable);
    }

    public long count() {
        return userRepository.count();
    }
}
