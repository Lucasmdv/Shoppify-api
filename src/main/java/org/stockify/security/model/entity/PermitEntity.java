package org.stockify.security.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.stockify.security.model.enums.Permit;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "permits")
public class PermitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "permit", nullable = false)
    private Permit permit;

    @PrePersist
    @PreUpdate
    private void ensureCode() {
        if (this.permit != null) {
            String normalized = this.permit.name().toLowerCase(java.util.Locale.ROOT);
            if (this.code == null || this.code.isBlank() || !this.code.equals(normalized)) {
                this.code = normalized;
            }
        }
    }


}
