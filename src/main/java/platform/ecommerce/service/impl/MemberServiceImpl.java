package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.ChangePasswordRequestDto;
import platform.ecommerce.dto.MemberResponseDto;
import platform.ecommerce.dto.UpdateMemberRequestDto;
import platform.ecommerce.entity.Member;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.service.MemberService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDto findMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        return MemberResponseDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .build();
    }

    @Override
    public Long updateMember(Long memberId, UpdateMemberRequestDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        member.updateMemberInfo(dto.getUsername(), dto.getEmail());
        Member updatedMember = memberRepository.save(member);

        log.info("Member [{}] updated successfully", updatedMember.getId());
        return updatedMember.getId();
    }

    @Override
    public Long changePassword(Long memberId, ChangePasswordRequestDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        //현재 비밀번호 검증
        if (!passwordEncoder.matches(dto.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        member.changePassword(encodedPassword);
        Member updatedMember = memberRepository.save(member);

        log.info("Password for Member [{}] updated successfully", updatedMember.getId());
        return updatedMember.getId();
    }

    @Override
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        memberRepository.delete(member);

        log.info("Member [{}] deleted successfully", memberId);
    }
}
