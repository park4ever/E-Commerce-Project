package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import platform.ecommerce.dto.MemberResponseDto;
import platform.ecommerce.entity.Member;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.service.MemberService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberResponseDto findMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        return MemberResponseDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .build();
    }
}
