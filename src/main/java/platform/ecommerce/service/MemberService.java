package platform.ecommerce.service;

import platform.ecommerce.dto.MemberResponseDto;
import platform.ecommerce.entity.Member;

import java.util.Optional;

public interface MemberService {

    MemberResponseDto findMember(String email);
}
