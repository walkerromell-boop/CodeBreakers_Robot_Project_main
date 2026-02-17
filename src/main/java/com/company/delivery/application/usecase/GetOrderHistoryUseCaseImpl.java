package com.company.delivery.application.usecase;

import com.company.delivery.application.dto.OrderDto;
import com.company.delivery.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Use case for retrieving order history.
 * History = DELIVERED and CANCELLED orders.
 */
@Service
@Transactional(readOnly = true)
public class GetOrderHistoryUseCaseImpl {

    private final OrderRepository orderRepository;

    public GetOrderHistoryUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository);
    }

    public List<OrderDto> execute() {
        return orderRepository.findHistory()
            .stream()
            .map(OrderDto::from)
            .collect(Collectors.toList());
    }
}
