package platform.ecommerce.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import platform.ecommerce.entity.Address;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberRequestDto {

    @Size(min = 2, max = 8, message = "사용자 이름은 2자 이상 8자 이하이어야 합니다.")
    private String username;

    private String phoneNumber;

    private Address address;

    private String dateOfBirth;

    private String newPassword;

    private String confirmNewPassword;

    //LocalDate -> String 변환 메서드
    public static String formatLocalDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    //String -> LocalDate 변환 메서드
    public LocalDate getDateOfBirthAsLocalDate() {
        if (this.dateOfBirth == null || this.dateOfBirth.isEmpty()) {
            return null;
        }
        return LocalDate.parse(this.dateOfBirth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
