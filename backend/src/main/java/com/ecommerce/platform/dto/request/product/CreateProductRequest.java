package com.ecommerce.platform.dto.request.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    private String description;

    private String imageUrl;

    @NotNull    
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull
    @PositiveOrZero
    private Integer quantity;

    @NotNull
    private Long categoryId;

}