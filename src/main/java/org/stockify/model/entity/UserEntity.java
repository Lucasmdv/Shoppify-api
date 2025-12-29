package org.stockify.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.stockify.security.model.entity.CredentialsEntity;

import java.time.LocalDate;
import java.util.Set;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_first_name", nullable = false, length = 20)
    private String firstName;


    @Column(name = "user_last_name", nullable = false, length = 20)
    private String lastName;


    @Column(name = "user_dni", nullable = false, length = 8, unique = true)
    private String dni;


    @Column(name = "user_phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "user_date_of_registration")
    @CreationTimestamp
    private LocalDate dateOfRegistration;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<SaleEntity> sales;

    @OneToOne(mappedBy = "user")
    private CredentialsEntity credentials;

    @OneToOne(mappedBy = "user")
    private CartEntity cart ;

    @Column(name = "user_img")
    private String img;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private WishlistEntity wishlist;

}
