package com.company.delivery.application.dto;

import com.company.delivery.domain.model.Robot;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for robot information.
 */
public record RobotDto(
    UUID id,
    String name,
    int batteryPercentage,
    String status,
    UUID assignedOrderId,
    String currentLocation,
    String targetLocation,
    boolean availableForDeployment,
    Instant lastUpdated
) {

    public static RobotDto from(Robot robot) {
        return new RobotDto(
            robot.getId(),
            robot.getName(),
            robot.getBatteryPercentage(),
            robot.getStatus().name(),
            robot.getAssignedOrderId(),
            robot.getCurrentLocation(),
            robot.getTargetLocation(),
            robot.isAvailableForDeployment(),
            robot.getLastUpdated()
        );
    }
}
