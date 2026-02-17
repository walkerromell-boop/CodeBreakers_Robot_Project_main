package com.company.delivery.application.usecase;

import com.company.delivery.application.dto.OrderDto;
import com.company.delivery.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Use case for listing all active orders.
 * Active = PENDING, CONFIRMED, or DISPATCHED (not DELIVERED or CANCELLED).
 */
@Service
@Transactional(readOnly = true)
public class ListActiveOrdersUseCaseImpl {

    private final OrderRepository orderRepository;

    public ListActiveOrdersUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository);
    }

    public List<OrderDto> execute() {
        return orderRepository.findActive()
            .stream()
            .map(OrderDto::from)
            .collect(Collectors.toList());
    }
}
