package com.ecommerce.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(
        name = "order_item",
        indexes = {
                @Index(
                        name = "idx_order_item_order",
                        columnList = "order_id"
                ),
                @Index(
                        name = "idx_order_item_product",
                        columnList = "product_id"
                )
        }
)
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_item_order")
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_item_product")
    )
    private Product product;

    @NotNull
    @Positive
    @Column(
            name = "quantity",
            nullable = false
    )
    private Integer quantity;

    @NotNull
    @DecimalMin("0.0")
    @Column(
            name = "price",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal price;

}