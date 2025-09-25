package org.stockify.model.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.stockify.controller.UserController;
import org.stockify.dto.request.user.UserFilterRequest;
import org.stockify.dto.response.UserResponse;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(UserResponse clientResponse) {
        return EntityModel.of(clientResponse,
                linkTo(methodOn(UserController.class).getUserById(clientResponse.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers(new UserFilterRequest(),0, 20, null)).withRel("clients"),
                linkTo(methodOn(UserController.class).deleteUserById(clientResponse.getId())).withRel("delete"),
                linkTo(methodOn(UserController.class).putUser(clientResponse.getId(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).patchUser(clientResponse.getId(), null)).withRel("partial-update"));
    }
}