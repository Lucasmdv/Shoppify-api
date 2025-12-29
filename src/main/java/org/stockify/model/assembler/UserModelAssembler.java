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
    public EntityModel<UserResponse> toModel(UserResponse userResponse) {
        return EntityModel.of(userResponse,
                linkTo(methodOn(UserController.class).getUserById(userResponse.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers(new UserFilterRequest(),0, 20, null)).withRel("users"),
                linkTo(methodOn(UserController.class).deleteUserById(userResponse.getId())).withRel("delete"),
                linkTo(methodOn(UserController.class).putUser(userResponse.getId(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).patchUser(userResponse.getId(), null)).withRel("partial-update"));
    }
}
