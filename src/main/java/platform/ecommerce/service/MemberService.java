package platform.ecommerce.service;

import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.member.MemberProfileDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.dto.member.UpdateMemberRequestDto;

public interface MemberService {

    MemberResponseDto findMember(String email);

    MemberDetailsDto findMemberDetails(String email);

    void deleteMember(Long memberId);

    UpdateMemberRequestDto toUpdateDto(Long memberId);

    MemberProfileDto toProfileDto(Long memberId);

    boolean checkPassword(Long memberId, String inputPassword);

    void updateMember(Long memberId, UpdateMemberRequestDto dto);
}
