package platform.ecommerce.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import platform.ecommerce.entity.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMemberDto {

    private Long id;
    private String email;
    private String username;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Role role;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @JsonProperty("isActive")
    private boolean isActive;
}
