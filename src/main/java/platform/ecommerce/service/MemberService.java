package platform.ecommerce.service;

import platform.ecommerce.dto.ChangePasswordRequestDto;
import platform.ecommerce.dto.MemberResponseDto;
import platform.ecommerce.dto.UpdateMemberRequestDto;
import platform.ecommerce.entity.Member;

import java.util.Optional;

public interface MemberService {

    MemberResponseDto findMember(String email);

    Long updateMember(Long memberId, UpdateMemberRequestDto dto);

    Long changePassword(Long memberId, ChangePasswordRequestDto dto);

    void deleteMember(Long memberId);
}
