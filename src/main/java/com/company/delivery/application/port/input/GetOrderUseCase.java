package com.company.delivery.application.port.input;

import com.company.delivery.application.dto.OrderDto;
import java.util.UUID;

public interface GetOrderUseCase {
    OrderDto execute(UUID orderId);
}