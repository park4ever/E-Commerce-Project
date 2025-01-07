package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.member.MemberSaveRequestDto;
import platform.ecommerce.entity.Address;
import platform.ecommerce.entity.Member;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.service.GlobalService;

import static platform.ecommerce.entity.Role.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalServiceImpl implements GlobalService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Long join(MemberSaveRequestDto dto) {

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        Member member = Member.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(encodedPassword)
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .dateOfBirth(dto.getDateOfBirth())
                .defaultShippingAddress(dto.getDefaultShippingAddress())
                .role(USER)
                .build();

        return memberRepository.save(member).getId();
    }

    @Override
    public boolean emailExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}