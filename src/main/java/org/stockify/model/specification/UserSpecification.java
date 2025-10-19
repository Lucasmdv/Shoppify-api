package org.stockify.model.specification;

import org.springframework.data.jpa.domain.Specification;
import org.stockify.model.entity.UserEntity;

public class UserSpecification {
    public static Specification<UserEntity> lastNameLike(String lastName) {
        return (root, query, criteriaBuilder) ->
                lastName == null ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    // int ? Integer ? String
    public static Specification<UserEntity> dniEquals(String dni) {
        return (root, query, criteriaBuilder) ->
                dni == null ? null :
                        criteriaBuilder.equal(root.get("dni"), dni);
    }

    public static Specification<UserEntity> phoneLike(String phone) {
        return (root, query, criteriaBuilder) ->
                phone == null ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
    }

    public static Specification<UserEntity> firstNameLike(String firstName) {
        return (root, query, criteriaBuilder) ->
                firstName == null ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }
}
