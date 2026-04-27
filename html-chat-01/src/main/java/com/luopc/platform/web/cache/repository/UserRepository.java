package com.luopc.platform.web.cache.repository;

import com.luopc.platform.web.cache.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByName(String name);

    List<User> findByAge(Integer age);

    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    @Query("{ 'age': { $gt: ?0 } }")
    List<User> findByAgeGreaterThan(Integer age);

    @Query("{ $or: [ { 'name': ?0 }, { 'email': ?1 } ] }")
    List<User> findByNameOrEmail(String name, String email);

    @Query("{ 'name': ?0, 'age': { $gt: ?1 } }")
    Page<User> findByNameAndAgeGreaterThan(String name, Integer minAge, Pageable pageable);
}
