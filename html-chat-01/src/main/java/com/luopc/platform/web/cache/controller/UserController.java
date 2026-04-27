package com.luopc.platform.web.cache.controller;

import com.luopc.platform.web.cache.model.User;
import com.luopc.platform.web.cache.service.UserService;
import com.luopc.platform.web.common.core.exception.PlatformErrorCode;
import com.luopc.platform.web.result.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "User manager endpoint")
public class UserController {

    private final UserService userService;

    @Operation(summary = "createUser")
    @Parameters({
            @Parameter(name = "user", description = "user")
    })
    @PostMapping
    public ResponseMessage<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseMessage.success(created);
    }

    @Operation(summary = "updateUser")
    @Parameters({
            @Parameter(name = "id", description = "id"),
            @Parameter(name = "user", description = "user")
    })
    @PutMapping("/{id}")
    public ResponseMessage<User> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        User updated = userService.updateUser(user);
        return ResponseMessage.success(updated);
    }

    @Operation(summary = "deleteUser")
    @Parameters({
            @Parameter(name = "id", description = "id")
    })
    @DeleteMapping("/{id}")
    public ResponseMessage<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseMessage.success();
    }

    @Operation(summary = "getById")
    @Parameters({
            @Parameter(name = "id", description = "id")
    })
    @GetMapping("/{id}")
    public ResponseMessage<User> getById(@PathVariable String id) {
        String defaultCode = String.valueOf(PlatformErrorCode.USER_STATUS_ERROR.getCode());
        return userService.findById(id)
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error(defaultCode, "User not found"));
    }

    @Operation(summary = "getByName")
    @Parameters({
            @Parameter(name = "name", description = "name")
    })
    @GetMapping("/byname/{name}")
    public ResponseMessage<User> getByName(@PathVariable String name) {
        String defaultCode = String.valueOf(PlatformErrorCode.USER_STATUS_ERROR.getCode());
        return userService.findByName(name)
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error(defaultCode, "User not found"));
    }

    @Operation(summary = "getAll")
    @GetMapping
    public ResponseMessage<List<User>> getAll() {
        List<User> users = userService.findAll();
        return ResponseMessage.success(users);
    }


    @Operation(summary = "getByAge")
    @Parameters({
            @Parameter(name = "age", description = "age")
    })
    @GetMapping("/by-age/{age}")
    public ResponseMessage<List<User>> getByAge(@PathVariable Integer age) {
        List<User> users = userService.findByAge(age);
        return ResponseMessage.success(users);
    }


    @Operation(summary = "getByAgeBetween")
    @Parameters({
            @Parameter(name = "minAge", description = "minAge"),
            @Parameter(name = "maxAge", description = "maxAge")
    })
    @GetMapping("/age-between")
    public ResponseMessage<List<User>> getByAgeBetween(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        List<User> users = userService.findByAgeBetween(minAge, maxAge);
        return ResponseMessage.success(users);
    }

    @Operation(summary = "getByAgeGreaterThan")
    @Parameters({
            @Parameter(name = "age", description = "age")
    })
    @GetMapping("/age-greater-than")
    public ResponseMessage<List<User>> getByAgeGreaterThan(@RequestParam Integer age) {
        List<User> users = userService.findByAgeGreaterThan(age);
        return ResponseMessage.success(users);
    }


    @Operation(summary = "search")
    @Parameters({
            @Parameter(name = "name", description = "name"),
            @Parameter(name = "email", description = "email")
    })
    @GetMapping("/search")
    public ResponseMessage<List<User>> searchByNameOrEmail(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        List<User> users = userService.findByNameOrEmail(name, email);
        return ResponseMessage.success(users);
    }



    @Operation(summary = "getPage")
    @Parameters({
            @Parameter(name = "page", description = "page"),
            @Parameter(name = "size", description = "size"),
            @Parameter(name = "sortBy", description = "sortBy")
    })
    @GetMapping("/page")
    public ResponseMessage<Page<User>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy) {
        Page<User> users = userService.findPage(page, size, sortBy);
        return ResponseMessage.success(users);
    }



    @Operation(summary = "getPageWithCondition")
    @Parameters({
            @Parameter(name = "name", description = "name"),
            @Parameter(name = "minAge", description = "minAge"),
            @Parameter(name = "page", description = "page"),
            @Parameter(name = "size", description = "size")
    })
    @GetMapping("/page/condition")
    public ResponseMessage<Page<User>> getPageWithCondition(
            @RequestParam String name,
            @RequestParam Integer minAge,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> users = userService.findPageWithCondition(name, minAge, page, size);
        return ResponseMessage.success(users);
    }

    @Operation(summary = "getCount")
    @GetMapping("/count")
    public ResponseMessage<Long> getCount() {
        return ResponseMessage.success(userService.count());
    }
}
