package com.luopc.platform.web.redis.controller;


import com.luopc.platform.web.common.core.exception.PlatformErrorCode;
import com.luopc.platform.web.redis.model.User;
import com.luopc.platform.web.redis.service.UserService;
import com.luopc.platform.web.result.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseMessage<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseMessage.success(created);
    }

    @PutMapping("/{id}")
    public ResponseMessage<User> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        User updated = userService.updateUser(user);
        return ResponseMessage.success(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseMessage<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseMessage.success();
    }

    @GetMapping("/{id}")
    public ResponseMessage<User> getById(@PathVariable String id) {
        String defaultCode = String.valueOf(PlatformErrorCode.USER_STATUS_ERROR.getCode());
        return userService.findById(id)
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error(defaultCode, "User not found"));
    }

    @GetMapping("/byname/{name}")
    public ResponseMessage<User> getByName(@PathVariable String name) {
        String defaultCode = String.valueOf(PlatformErrorCode.USER_STATUS_ERROR.getCode());
        return userService.findByName(name)
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error(defaultCode, "User not found"));
    }

    @GetMapping("/by-email/{email}")
    public ResponseMessage<User> getByEmail(@PathVariable String email) {
        String defaultCode = String.valueOf(PlatformErrorCode.USER_STATUS_ERROR.getCode());
        return userService.findByEmail(email)
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error(defaultCode, "User not found"));
    }

    @GetMapping
    public ResponseMessage<List<User>> getAll() {
        List<User> users = userService.findAll();
        return ResponseMessage.success(users);
    }

    @GetMapping("/by-age/{age}")
    public ResponseMessage<List<User>> getByAge(@PathVariable Integer age) {
        List<User> users = userService.findByAge(age);
        return ResponseMessage.success(users);
    }

    @GetMapping("/age-between")
    public ResponseMessage<List<User>> getByAgeBetween(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        List<User> users = userService.findByAgeBetween(minAge, maxAge);
        return ResponseMessage.success(users);
    }

    @GetMapping("/age-greater-than")
    public ResponseMessage<List<User>> getByAgeGreaterThan(@RequestParam Integer age) {
        List<User> users = userService.findByAgeGreaterThan(age);
        return ResponseMessage.success(users);
    }

    @GetMapping("/search/name")
    public ResponseMessage<List<User>> searchByName(@RequestParam String name) {
        List<User> users = userService.findByNameContaining(name);
        return ResponseMessage.success(users);
    }

    @GetMapping("/count")
    public ResponseMessage<Long> getCount() {
        return ResponseMessage.success(userService.count());
    }

    @DeleteMapping("/all")
    public ResponseMessage<Void> deleteAll() {
        userService.deleteAll();
        return ResponseMessage.success();
    }
}
