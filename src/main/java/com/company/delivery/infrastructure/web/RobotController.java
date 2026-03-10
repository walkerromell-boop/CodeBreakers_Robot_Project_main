package com.company.delivery.infrastructure.web;

import com.company.delivery.application.dto.RobotDto;
import com.company.delivery.application.usecase.DeployRobotUseCaseImpl;
import com.company.delivery.application.usecase.ListRobotsUseCaseImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for robot management.
 */
@RestController
@RequestMapping("/api/v1/robots")
public class RobotController {

    private final ListRobotsUseCaseImpl listRobotsUseCase;
    private final DeployRobotUseCaseImpl deployRobotUseCase;

    public RobotController(ListRobotsUseCaseImpl listRobotsUseCase,
                          DeployRobotUseCaseImpl deployRobotUseCase) {
        this.listRobotsUseCase = listRobotsUseCase;
        this.deployRobotUseCase = deployRobotUseCase;
    }

    /**
     * Get all robots in the system.
     * 
     * GET /api/v1/robots
     */
    @GetMapping
    public ResponseEntity<List<RobotDto>> getAllRobots() {
        List<RobotDto> robots = listRobotsUseCase.execute();
        return ResponseEntity.ok(robots);
    }

    /**
     * Deploy a robot to deliver an order.
     * Staff only.
     * 
     * POST /api/v1/robots/deploy
     * Body: {"orderId": "uuid", "deliveryAddress": "123 Main St"}
     */
    @PostMapping("/deploy")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> deployRobot(@RequestBody DeployRobotRequest request) {
        try {
            RobotDto robot = deployRobotUseCase.execute(
                request.orderId(),
                request.deliveryAddress()
            );
            return ResponseEntity.ok(robot);
        } catch (DeployRobotUseCaseImpl.NoRobotsAvailableException e) {
            return ResponseEntity.status(503).body(Map.of(
                "error", "SERVICE_UNAVAILABLE",
                "message", e.getMessage()
            ));
        }
    }

    record DeployRobotRequest(UUID orderId, String deliveryAddress) {}
}
