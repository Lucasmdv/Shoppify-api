package org.stockify.security.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RoleAssignRequest", description = "DTO to assign a role to a user by email (by role id)")
public class RoleAssignRequest {
    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "Role id to assign", example = "3")
    private Long roleId;
}
