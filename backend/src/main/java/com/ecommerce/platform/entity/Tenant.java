package com.ecommerce.platform.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(
        name = "tenant",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tenant_domain",
                        columnNames = "domain"
                )
        }
)
public class Tenant extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(
            name = "name",
            nullable = false,
            length = 100
    )
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(
            name = "domain",
            nullable = false,
            length = 100
    )
    private String domain;

    @Builder.Default
    @Column(
            name = "enabled",
            nullable = false
    )
    private Boolean enabled = true;

    @OneToMany(
            mappedBy = "tenant",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(
            mappedBy = "tenant",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @OneToMany(
            mappedBy = "tenant",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Product> products = new ArrayList<>();

}