package com.ecommerce.platform.dto.response;

import com.ecommerce.platform.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderResponse {

    private Long id;

    private Integer totalQuantity;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private List<OrderItemResponse> items;

}