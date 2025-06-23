package platform.ecommerce.service;

import platform.ecommerce.dto.member.*;

public interface MemberService {

    MemberResponseDto findMember(String email);

    MemberDetailsDto findMemberDetails(String email);

    LoginMemberDto findLoginMember(String username);

    MemberDetailsDto findMemberDetailsOrDefault(String email);

    void deleteMember(Long memberId);

    UpdateMemberRequestDto toUpdateDto(Long memberId);

    MemberProfileDto toProfileDto(Long memberId);

    boolean checkPassword(Long memberId, String inputPassword);

    void updateMember(Long memberId, UpdateMemberRequestDto dto);
}
