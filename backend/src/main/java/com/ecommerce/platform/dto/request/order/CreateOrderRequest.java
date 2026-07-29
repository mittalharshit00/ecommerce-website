package com.ecommerce.platform.dto.request.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @Valid
    @NotEmpty
    private List<OrderItemRequest> items;

}