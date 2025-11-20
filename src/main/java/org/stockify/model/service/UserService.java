package org.stockify.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.stockify.dto.request.user.UserFilterRequest;
import org.stockify.dto.request.user.UserRequest;
import org.stockify.dto.response.UserResponse;
import org.stockify.model.entity.UserEntity;
import org.stockify.model.exception.UserNotFoundException;
import org.stockify.model.mapper.UserMapper;
import org.stockify.model.repository.UserRepository;
import org.stockify.model.specification.UserSpecification;
 

/**
 * Service class responsible for managing user-related operations,
 * including searching, saving, updating, and deleting users.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CartService cartService;
    //private final WishlistService wishlistService;
    // Removed security-related dependencies from UserService

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find
     * @return a DTO containing the user data
     * @throws UserNotFoundException if no user is found with the specified ID
     */
    public UserResponse findById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return userMapper.toDto(userEntity);
    }

    /**
     * Searches for users matching the given filter criteria with pagination.
     *
     * @param filterRequest DTO containing filter criteria (first name, last name, DNI, phone)
     * @param pageable      pagination information
     * @return a paginated list of users matching the filters
     */
    public Page<UserResponse> findAll(UserFilterRequest filterRequest, Pageable pageable) {
        Specification<UserEntity> specification = Specification
                .where(UserSpecification.firstNameLike(filterRequest.getFirstName()))
                .and(UserSpecification.lastNameLike(filterRequest.getLastName()))
                .and(UserSpecification.dniEquals(filterRequest.getDni()))
                .and(UserSpecification.phoneLike(filterRequest.getPhone()));

        Page<UserEntity> users = userRepository.findAll(specification, pageable);
        return users.map(userMapper::toDto);
    }

    /**
     * Saves a new user in the system.
     *
     * @param userRequest DTO containing the user data to create
     * @return a DTO with the saved user data
     */
    public UserResponse save(UserRequest userRequest) {

        UserEntity userEntity = userMapper.toEntity(userRequest);
        UserEntity createdUser = userRepository.save(userEntity);
        cartService.createCart(createdUser.getId());
       // wishlistService.createWishlist(createdUser.getId());
        return userMapper.toDto(createdUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @throws UserNotFoundException if no user is found with the specified ID
     */
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Partially updates an existing user with the provided data.
     *
     * @param id            the ID of the user to update partially
     * @param userRequest DTO containing the fields to update
     * @return a DTO with the updated user data
     * @throws UserNotFoundException if no user is found with the specified ID
     */
    public UserResponse updateUserPartial(Long id, UserRequest userRequest) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        userMapper.partialUpdateUserEntity(userRequest, existingUser);

        UserEntity updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    /**
     * Fully updates an existing user with the provided data.
     *
     * @param id            the ID of the user to update
     * @param userRequest DTO containing the new user data
     * @return a DTO with the updated user data
     * @throws UserNotFoundException if no user is found with the specified ID
     */
    public UserResponse updateUserFull(Long id, UserRequest userRequest) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        userMapper.updateUserEntity(userRequest, existingUser);

        UserEntity updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    // Removed token generation, credential management, and permit helper logic from UserService.
}
