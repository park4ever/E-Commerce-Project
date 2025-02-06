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
        Member member = findMemberByEmail(email);

        return MemberResponseDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDetailsDto findMemberDetails(String email) {
        Member member = findMemberByEmail(email);

        return MemberDetailsDto.builder()
                .username(member.getUsername())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .build();
    }

    @Override
    public void deleteMember(Long memberId) {
        memberRepository.delete(findMemberById(memberId));
        log.info("Member [{}] deleted successfully", memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public UpdateMemberRequestDto toUpdateDto(Long memberId) {
        Member member = findMemberById(memberId);

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
        Member member = findMemberById(memberId);

        return MemberProfileDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .dateOfBirth(member.getDateOfBirth())
                .defaultShippingAddress(member.getDefaultShippingAddress())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkPassword(Long memberId, String inputPassword) {
        return passwordEncoder.matches(inputPassword, findMemberById(memberId).getPassword());
    }

    @Override
    public void updateMember(Long memberId, UpdateMemberRequestDto dto) {
        Member member = findMemberById(memberId);

        member.updateMemberInfo(dto.getUsername(), dto.getPhoneNumber(), dto.getAddress(), dto.getDateOfBirth());

        memberRepository.save(member);
        log.info("Member [{}] updated successfully!", member.getId());
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
    }
}
