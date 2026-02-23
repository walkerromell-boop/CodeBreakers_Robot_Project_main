package com.company.delivery.infrastructure.persistence;

import com.company.delivery.domain.model.User;
import com.company.delivery.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User>   byId        = new ConcurrentHashMap<>();
    private final Map<String, User> byStudentId = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        byId.put(user.getId(), user);
        byStudentId.put(user.getStudentId(), user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<User> findByStudentId(String studentId) {
        return Optional.ofNullable(byStudentId.get(studentId));
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        return byStudentId.containsKey(studentId);
    }

    @Override
    public Optional<User> findByResetToken(String resetToken) {
        // Scan all users for matching token - fine for in-memory demo
        return byId.values().stream()
            .filter(u -> resetToken.equals(u.getResetToken()))
            .findFirst();
    }
}

//This is a test