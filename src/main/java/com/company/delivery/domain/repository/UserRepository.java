package com.company.delivery.domain.repository;

import com.company.delivery.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(UUID id);

    /** Used at login */
    Optional<User> findByStudentId(String studentId);

    /** Used at registration to prevent duplicates */
    boolean existsByStudentId(String studentId);

    /** Used during password reset to validate the token */
    Optional<User> findByResetToken(String resetToken);
}
