package com.luopc.platform.web.redis.repository;


import com.luopc.platform.web.redis.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_KEY_PREFIX = "user:";
    private static final String USER_INDEX_NAME = "user:index:name:";
    private static final String USER_INDEX_EMAIL = "user:index:email:";
    private static final String USER_SET_ALL = "user:set:all";

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
        }
        user.setUpdateTime(LocalDateTime.now());

        String key = USER_KEY_PREFIX + user.getId();
        redisTemplate.opsForHash().putAll(key, convertToMap(user));
        redisTemplate.expire(key, 30, TimeUnit.DAYS);

        addToSet(user.getId());
        indexUser(user);

        return user;
    }

    public Optional<User> findById(String id) {
        String key = USER_KEY_PREFIX + id;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(convertToUser(entries));
    }

    public Optional<User> findByName(String name) {
        Set<Object> userIds = redisTemplate.opsForSet().members(USER_INDEX_NAME + name);
        if (userIds == null || userIds.isEmpty()) {
            return Optional.empty();
        }
        String userId = userIds.iterator().next().toString();
        return findById(userId);
    }

    public Optional<User> findByEmail(String email) {
        String userId = (String) redisTemplate.opsForValue().get(USER_INDEX_EMAIL + email);
        if (userId == null) {
            return Optional.empty();
        }
        return findById(userId);
    }

    public List<User> findAll() {
        Set<Object> userIds = redisTemplate.opsForSet().members(USER_SET_ALL);
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> keys = userIds.stream()
                .map(id -> USER_KEY_PREFIX + id)
                .collect(Collectors.toList());

        return keys.stream()
                .map(key -> {
                    Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
                    return convertToUser(entries);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> findByAge(Integer age) {
        return findAll().stream()
                .filter(user -> age.equals(user.getAge()))
                .collect(Collectors.toList());
    }

    public List<User> findByAgeBetween(Integer minAge, Integer maxAge) {
        return findAll().stream()
                .filter(user -> user.getAge() >= minAge && user.getAge() <= maxAge)
                .collect(Collectors.toList());
    }

    public List<User> findByAgeGreaterThan(Integer age) {
        return findAll().stream()
                .filter(user -> user.getAge() > age)
                .collect(Collectors.toList());
    }

    public List<User> findByNameContaining(String name) {
        return findAll().stream()
                .filter(user -> user.getName() != null && user.getName().contains(name))
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        String key = USER_KEY_PREFIX + id;
        User user = findById(id).orElse(null);

        if (user != null) {
            removeIndex(user);
        }

        redisTemplate.delete(key);
        removeFromSet(id);
    }

    public void deleteAll() {
        Set<Object> userIds = redisTemplate.opsForSet().members(USER_SET_ALL);
        if (userIds != null) {
            for (Object userId : userIds) {
                deleteById(userId.toString());
            }
        }
    }

    public long count() {
        Long size = redisTemplate.opsForSet().size(USER_SET_ALL);
        return size != null ? size : 0;
    }

    public boolean existsById(String id) {
        String key = USER_KEY_PREFIX + id;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private void addToSet(String userId) {
        redisTemplate.opsForSet().add(USER_SET_ALL, userId);
    }

    private void removeFromSet(String userId) {
        redisTemplate.opsForSet().remove(USER_SET_ALL, userId);
    }

    private void indexUser(User user) {
        if (user.getName() != null) {
            redisTemplate.opsForSet().add(USER_INDEX_NAME + user.getName(), user.getId());
        }
        if (user.getEmail() != null) {
            redisTemplate.opsForValue().set(USER_INDEX_EMAIL + user.getEmail(), user.getId());
        }
    }

    private void removeIndex(User user) {
        if (user.getName() != null) {
            redisTemplate.opsForSet().remove(USER_INDEX_NAME + user.getName(), user.getId());
        }
        if (user.getEmail() != null) {
            redisTemplate.delete(USER_INDEX_EMAIL + user.getEmail());
        }
    }

    private Map<String, Object> convertToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("age", user.getAge());
        map.put("email", user.getEmail());
        map.put("createTime", user.getCreateTime() != null ? user.getCreateTime().toString() : null);
        map.put("updateTime", user.getUpdateTime() != null ? user.getUpdateTime().toString() : null);
        return map;
    }

    private User convertToUser(Map<Object, Object> entries) {
        if (entries.isEmpty()) {
            return null;
        }
        return User.builder()
                .id((String) entries.get("id"))
                .name((String) entries.get("name"))
                .age((Integer) entries.get("age"))
                .email((String) entries.get("email"))
                .createTime(parseLocalDateTime((String) entries.get("createTime")))
                .updateTime(parseLocalDateTime((String) entries.get("updateTime")))
                .build();
    }

    private LocalDateTime parseLocalDateTime(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(str);
        } catch (Exception e) {
            return null;
        }
    }
}
