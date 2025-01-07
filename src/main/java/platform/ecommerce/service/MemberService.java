package platform.ecommerce.service;

import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.member.MemberProfileDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.dto.member.UpdateMemberRequestDto;

public interface MemberService {

    MemberResponseDto findMember(String email);

    MemberDetailsDto findMemberDetails(String email);

    Long updateMemberWithPasswordCheck(Long memberId, UpdateMemberRequestDto dto, String currentPassword);

    void deleteMember(Long memberId);

    UpdateMemberRequestDto toUpdateDto(Long memberId);

    MemberProfileDto toProfileDto(Long memberId);
}
