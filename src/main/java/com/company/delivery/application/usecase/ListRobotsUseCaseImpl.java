package com.company.delivery.application.usecase;

import com.company.delivery.application.dto.RobotDto;
import com.company.delivery.domain.model.Robot;
import com.company.delivery.domain.repository.RobotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Get all robots in the system.
 */
@Service
@Transactional(readOnly = true)
public class ListRobotsUseCaseImpl {

    private final RobotRepository robotRepository;

    public ListRobotsUseCaseImpl(RobotRepository robotRepository) {
        this.robotRepository = Objects.requireNonNull(robotRepository);
    }

    public List<RobotDto> execute() {
        return robotRepository.findAll()
            .stream()
            .map(RobotDto::from)
            .collect(Collectors.toList());
    }
}
