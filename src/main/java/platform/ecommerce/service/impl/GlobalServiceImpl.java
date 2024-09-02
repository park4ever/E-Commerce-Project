package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.MemberSaveRequestDto;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.Role;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.service.GlobalService;

import static platform.ecommerce.entity.Role.*;

@Service
@RequiredArgsConstructor
public class GlobalServiceImpl implements GlobalService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Long join(MemberSaveRequestDto memberSaveRequestDto) {
        String encodedPassword = passwordEncoder.encode(memberSaveRequestDto.getPassword());

        Member member = Member.builder()
                .username(memberSaveRequestDto.getUsername())
                .email(memberSaveRequestDto.getEmail())
                .password(encodedPassword)
                .role(USER)
                .build();

        return memberRepository.save(member).getId();
    }
}