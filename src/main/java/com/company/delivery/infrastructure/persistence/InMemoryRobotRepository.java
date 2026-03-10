package com.company.delivery.infrastructure.persistence;

import com.company.delivery.domain.model.Robot;
import com.company.delivery.domain.model.RobotStatus;
import com.company.delivery.domain.repository.RobotRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of RobotRepository.
 * Pre-loaded with 3 demo robots.
 */
@Repository
public class InMemoryRobotRepository implements RobotRepository {

    private final Map<UUID, Robot> store = new ConcurrentHashMap<>();

    public InMemoryRobotRepository() {
        // Pre-load 3 demo robots
        Robot robot1 = Robot.create("DeliveryBot-001", "Campus Main Entrance");
        Robot robot2 = Robot.create("DeliveryBot-002", "Campus Main Entrance");
        Robot robot3 = Robot.create("DeliveryBot-003", "Campus Main Entrance");

        store.put(robot1.getId(), robot1);
        store.put(robot2.getId(), robot2);
        store.put(robot3.getId(), robot3);
    }

    @Override
    public void save(Robot robot) {
        store.put(robot.getId(), robot);
    }

    @Override
    public Optional<Robot> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Robot> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Robot> findByStatus(RobotStatus status) {
        return store.values().stream()
            .filter(robot -> robot.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public List<Robot> findAvailableRobots() {
        return store.values().stream()
            .filter(Robot::isAvailableForDeployment)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Robot> findByAssignedOrderId(UUID orderId) {
        return store.values().stream()
            .filter(robot -> orderId.equals(robot.getAssignedOrderId()))
            .findFirst();
    }
}
