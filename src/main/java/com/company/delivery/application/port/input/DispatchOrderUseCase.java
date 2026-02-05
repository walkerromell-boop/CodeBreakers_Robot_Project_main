package com.company.delivery.application.port.input;

import com.company.delivery.application.dto.OrderDto;
import java.util.UUID;

public interface DispatchOrderUseCase {
    OrderDto execute(UUID orderId);
}