package com.ostay.shiroweb.mapper;

import com.ostay.shiroweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserMapper extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM user WHERE name = ?1")
    User findByName(String name);
}
