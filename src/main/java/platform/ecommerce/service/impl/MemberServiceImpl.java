package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.member.MemberProfileDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.dto.member.UpdateMemberRequestDto;
import platform.ecommerce.entity.Address;
import platform.ecommerce.entity.Member;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.service.MemberService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
    @Transactional(readOnly = true)
    public MemberDetailsDto findMemberDetails(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        return MemberDetailsDto.builder()
                .username(member.getUsername())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .build();
    }

    @Override
    public Long updateMemberWithPasswordCheck(Long memberId, UpdateMemberRequestDto dto, String currentPassword) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        //현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        //회원 정보 업데이트
        member.updateMemberInfo(dto.getUsername(), dto.getPhoneNumber(), dto.getAddress(), dto.getDateOfBirth());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
            member.changePassword(encodedPassword);
        }

        log.info("Member [{}] updated successfully", member.getId());
        return memberRepository.save(member).getId();
    }

    @Override
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        memberRepository.delete(member);

        log.info("Member [{}] deleted successfully", memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public UpdateMemberRequestDto toUpdateDto(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        UpdateMemberRequestDto memberDto = new UpdateMemberRequestDto();
        memberDto.setUsername(member.getUsername());
        memberDto.setPhoneNumber(member.getPhoneNumber());
        memberDto.setAddress(member.getAddress());
        memberDto.setDateOfBirth(member.getDateOfBirth());

        return memberDto;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberProfileDto toProfileDto(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        return MemberProfileDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .dateOfBirth(member.getDateOfBirth())
                .defaultShippingAddress(member.getDefaultShippingAddress())
                .build();
    }
}
