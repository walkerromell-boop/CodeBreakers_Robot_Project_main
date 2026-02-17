package com.company.delivery.domain.repository;

import com.company.delivery.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for User persistence.
 */
public interface UserRepository {

    void save(User user);

    Optional<User> findById(UUID id);

    /** Used at login - look up by school student ID */
    Optional<User> findByStudentId(String studentId);

    /** Used at registration - check the student ID isn't already taken */
    boolean existsByStudentId(String studentId);
}
