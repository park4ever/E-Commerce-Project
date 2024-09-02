package platform.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSaveRequestDto {

    @NotEmpty
    private String username;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String password;
}
