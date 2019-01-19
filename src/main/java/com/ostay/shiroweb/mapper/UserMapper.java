package com.ostay.shiroweb.mapper;

import com.ostay.shiroweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMapper extends JpaRepository<User, Long> {
}
