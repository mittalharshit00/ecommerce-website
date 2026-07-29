package com.ecommerce.platform.mapper;

import com.ecommerce.platform.dto.response.OrderItemResponse;
import com.ecommerce.platform.dto.response.OrderResponse;
import com.ecommerce.platform.entity.Order;
import com.ecommerce.platform.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", source = "orderItems")
    OrderResponse toResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponse toResponse(OrderItem orderItem);

}