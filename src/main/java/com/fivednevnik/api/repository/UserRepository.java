package com.fivednevnik.api.repository;

import com.fivednevnik.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(User.Role role);

    List<User> findByRoleAndClassName(User.Role role, String className);

    List<User> findByRoleAndClassId(User.Role role, Long classId);

    Long countByRoleAndClassName(User.Role role, String className);
} 
