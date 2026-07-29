package com.ecommerce.platform.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private BigDecimal price;

    private Integer quantity;

    private Long categoryId;

    private String categoryName;

}