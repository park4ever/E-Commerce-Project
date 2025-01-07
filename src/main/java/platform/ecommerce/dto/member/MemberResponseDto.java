package platform.ecommerce.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
public class MemberResponseDto {

    private Long memberId;
    private String email;
    private String username;

    @Builder
    public MemberResponseDto(Long memberId, String email, String username) {
        this.memberId = memberId;
        this.email = email;
        this.username = username;
    }
}
