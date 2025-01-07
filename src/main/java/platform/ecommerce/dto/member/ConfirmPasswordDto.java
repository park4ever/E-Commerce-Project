package platform.ecommerce.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmPasswordDto {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;
}
