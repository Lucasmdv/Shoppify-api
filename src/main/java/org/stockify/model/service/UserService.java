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
import org.stockify.model.exception.ClientNotFoundException;
import org.stockify.model.mapper.UserMapper;
import org.stockify.model.repository.UserRepository;
import org.stockify.model.specification.UserSpecification;
 

/**
 * Service class responsible for managing client-related operations,
 * including searching, saving, updating, and deleting clients.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CartService cartService;
    // Removed security-related dependencies from UserService

    /**
     * Finds a client by their ID.
     *
     * @param id the ID of the client to find
     * @return a DTO containing the client data
     * @throws ClientNotFoundException if no client is found with the specified ID
     */
    public UserResponse findById(Long id) {
        UserEntity clientEntity = userRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client with id " + id + " not found"));
        return userMapper.toDto(clientEntity);
    }

    /**
     * Searches for clients matching the given filter criteria with pagination.
     *
     * @param filterRequest DTO containing filter criteria (first name, last name, DNI, phone)
     * @param pageable      pagination information
     * @return a paginated list of clients matching the filters
     */
    public Page<UserResponse> findAll(UserFilterRequest filterRequest, Pageable pageable) {
        Specification<UserEntity> specification = Specification
                .where(UserSpecification.firstNameLike(filterRequest.getFirstName()))
                .and(UserSpecification.lastNameLike(filterRequest.getLastName()))
                .and(UserSpecification.dniEquals(filterRequest.getDni()))
                .and(UserSpecification.phoneLike(filterRequest.getPhone()));

        Page<UserEntity> clients = userRepository.findAll(specification, pageable);
        return clients.map(userMapper::toDto);
    }

    /**
     * Saves a new client in the system.
     *
     * @param userRequest DTO containing the client data to create
     * @return a DTO with the saved client data
     */
    public UserResponse save(UserRequest userRequest) {

        UserEntity userEntity = userMapper.toEntity(userRequest);
        UserEntity createdUser = userRepository.save(userEntity);
        cartService.createUserCart(createdUser.getId());
        return userMapper.toDto(createdUser);
    }

    /**
     * Deletes a client by their ID.
     *
     * @param id the ID of the client to delete
     * @throws ClientNotFoundException if no client is found with the specified ID
     */
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ClientNotFoundException("Client with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Partially updates an existing client with the provided data.
     *
     * @param id            the ID of the client to update partially
     * @param clientRequest DTO containing the fields to update
     * @return a DTO with the updated client data
     * @throws ClientNotFoundException if no client is found with the specified ID
     */
    public UserResponse updateClientPartial(Long id, UserRequest clientRequest) {
        UserEntity existingClient = userRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client with id " + id + " not found"));

        userMapper.partialUpdateClientEntity(clientRequest, existingClient);

        UserEntity updatedClient = userRepository.save(existingClient);
        return userMapper.toDto(updatedClient);
    }

    /**
     * Fully updates an existing client with the provided data.
     *
     * @param id            the ID of the client to update
     * @param clientRequest DTO containing the new client data
     * @return a DTO with the updated client data
     * @throws ClientNotFoundException if no client is found with the specified ID
     */
    public UserResponse updateClientFull(Long id, UserRequest clientRequest) {
        UserEntity existingClient = userRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client with id " + id + " not found"));

        userMapper.updateClientEntity(clientRequest, existingClient);

        UserEntity updatedClient = userRepository.save(existingClient);
        return userMapper.toDto(updatedClient);
    }

    // Removed token generation, credential management, and permit helper logic from UserService.
}
