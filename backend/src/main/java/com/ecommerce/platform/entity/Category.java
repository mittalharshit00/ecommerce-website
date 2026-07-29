package com.ecommerce.platform.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "category", uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_tenant_name", columnNames = {
                                "tenant_id",
                                "name"
                })
}, indexes = {
                @Index(name = "idx_category_tenant", columnList = "tenant_id")
})
public class Category extends BaseEntity {

        @NotBlank
        @Size(max = 100)
        @Column(name = "name", nullable = false, length = 100)
        private String name;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "tenant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_category_tenant"))
        private Tenant tenant;

        @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        @Builder.Default
        private List<Product> products = new ArrayList<>();

}