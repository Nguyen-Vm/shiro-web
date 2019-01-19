package com.ostay.shiroweb.mapper;

import com.ostay.shiroweb.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleMapper extends JpaRepository<Role, Long> {
}
