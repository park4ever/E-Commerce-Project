package platform.ecommerce.dto.member;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import platform.ecommerce.entity.Address;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSaveRequestDto {

    @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 4, message = "사용자 이름은 2자 이상 4자 이하이어야 합니다.")
    @Pattern(regexp = "^[가-힣]+$", message = "사용자 이름은 한글만 입력 가능합니다.")
    private String username;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해 주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 위해 한번 더 입력해주세요.")
    private String passwordConfirm;

    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자만 입력 가능합니다.")
    private String phoneNumber;

    private Address address;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
}
