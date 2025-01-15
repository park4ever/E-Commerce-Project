package platform.ecommerce.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.Role;

@Getter
@NoArgsConstructor
public class MemberDto {

    private String email;
    private String password;
    private Role role;

    public MemberDto(Member member) {
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.role = member.getRole();
    }
}
