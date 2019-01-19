package com.ostay.shiroweb.mapper;

import com.ostay.shiroweb.model.RoleToUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * @author RWM
 * @date 2019/1/20
 */
public interface RoleToUserMapper extends JpaRepository<RoleToUser, Long> {

    @Query(nativeQuery = true, value = "select r.name from role r join role_to_user ru on r.id = ru.role_id " +
            "join user u on u.id = ru.user_id where u.name = ?1")
    Set<String> findByUsername(String username);
}
