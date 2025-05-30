package com.fivednevnik.api.repository;

import com.fivednevnik.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по имени
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Проверка существования пользователя по имени
     */
    boolean existsByUsername(String username);
    
    /**
     * Проверка существования пользователя по email
     */
    boolean existsByEmail(String email);
    
    /**
     * Поиск пользователей по роли
     */
    List<User> findByRole(User.Role role);
    
    /**
     * Поиск пользователей по роли и имени класса
     */
    List<User> findByRoleAndClassName(User.Role role, String className);
    
    /**
     * Подсчет пользователей по роли и имени класса
     */
    Long countByRoleAndClassName(User.Role role, String className);
} 