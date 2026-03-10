package com.company.delivery.domain.repository;

import com.company.delivery.domain.model.Robot;
import com.company.delivery.domain.model.RobotStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Robot persistence.
 */
public interface RobotRepository {

    void save(Robot robot);

    Optional<Robot> findById(UUID id);

    List<Robot> findAll();

    /** Find all robots with a specific status */
    List<Robot> findByStatus(RobotStatus status);

    /** Find robots available for deployment (IDLE + battery >= 20%) */
    List<Robot> findAvailableRobots();

    /** Find robot assigned to a specific order */
    Optional<Robot> findByAssignedOrderId(UUID orderId);
}
