package com.company.delivery.application.usecase;

import com.company.delivery.application.dto.RobotDto;
import com.company.delivery.domain.model.Robot;
import com.company.delivery.domain.repository.RobotRepository;
import com.company.delivery.domain.valueobject.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Deploy a robot to deliver an order.
 * 
 * Finds an available robot and assigns it to the order.
 */
@Service
@Transactional
public class DeployRobotUseCaseImpl {

    private final RobotRepository robotRepository;

    public DeployRobotUseCaseImpl(RobotRepository robotRepository) {
        this.robotRepository = Objects.requireNonNull(robotRepository);
    }

    /**
     * Deploy an available robot to deliver the order.
     *
     * @param orderId order to deliver
     * @param deliveryAddress where to deliver
     * @return the deployed robot
     * @throws NoRobotsAvailableException if no robots are available
     */
    public RobotDto execute(UUID orderId, String deliveryAddress) {
        Objects.requireNonNull(orderId, "Order ID required");
        Objects.requireNonNull(deliveryAddress, "Delivery address required");

        // Find an available robot
        List<Robot> availableRobots = robotRepository.findAvailableRobots();
        
        if (availableRobots.isEmpty()) {
            throw new NoRobotsAvailableException();
        }

        // Pick the first available robot
        Robot robot = availableRobots.get(0);

        // Deploy it
        robot.deploy(orderId, deliveryAddress);

        // Save
        robotRepository.save(robot);

        return RobotDto.from(robot);
    }

    public static class NoRobotsAvailableException extends RuntimeException {
        public NoRobotsAvailableException() {
            super("No robots available for deployment - all robots are busy or low on battery");
        }
    }
}
