package com.ecommerce.platform.entity;

import com.ecommerce.platform.enums.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(
        name = "role",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_role_name",
                        columnNames = "name"
                )
        },
        indexes = {
                @Index(
                        name = "idx_role_name",
                        columnList = "name"
                )
        }
)
public class Role extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(
            name = "name",
            nullable = false,
            length = 50
    )
    private RoleType name;

    @ManyToMany(
            mappedBy = "roles",
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<User> users = new HashSet<>();

}