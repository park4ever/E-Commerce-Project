package platform.ecommerce.dto.member;

public record LoginMemberDto (
        Long id,
        String username,
        String email,
        String role
) {}
