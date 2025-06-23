package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.member.*;
import platform.ecommerce.entity.Address;
import platform.ecommerce.entity.Member;
import platform.ecommerce.exception.member.MemberNotFoundException;
import platform.ecommerce.exception.member.MemberPasswordMismatchException;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.service.MemberService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberResponseDto findMember(String email) {
        Member member = findMemberByEmail(email);

        return MemberResponseDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .build();
    }

    @Override
    public MemberDetailsDto findMemberDetails(String email) {
        Member member = findMemberByEmail(email);
        if (member == null) {
            throw new MemberNotFoundException();
        }

        return MemberDetailsDto.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .build();
    }

    @Override
    public LoginMemberDto findLoginMember(String email) {
        Member member = findMemberByEmail(email);

        return new LoginMemberDto(
                member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getRole().name()
        );
    }

    @Override
    public MemberDetailsDto findMemberDetailsOrDefault(String email) {
        Member member = findMemberByEmail(email);

        if (member == null) {
            return MemberDetailsDto.builder()
                    .memberId(0L)
                    .username("")
                    .phoneNumber("")
                    .address(new Address("", "", "", ""))
                    .build();
        }

        return findMemberDetails(email);
    }

    @Override
    @Transactional
    public void deleteMember(Long memberId) {
        memberRepository.delete(findMemberById(memberId));
    }

    @Override
    public UpdateMemberRequestDto toUpdateDto(Long memberId) {
        Member member = findMemberById(memberId);

        UpdateMemberRequestDto memberDto = new UpdateMemberRequestDto();
        memberDto.setUsername(member.getUsername());
        memberDto.setPhoneNumber(member.getPhoneNumber());
        memberDto.setAddress(member.getAddress());

        memberDto.setDateOfBirth(UpdateMemberRequestDto.formatLocalDate(member.getDateOfBirth()));

        return memberDto;
    }

    @Override
    public MemberProfileDto toProfileDto(Long memberId) {
        Member member = findMemberById(memberId);

        return MemberProfileDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .dateOfBirth(member.getDateOfBirth())
                .build();
    }

    @Override
    public boolean checkPassword(Long memberId, String inputPassword) {
        return passwordEncoder.matches(inputPassword, findMemberById(memberId).getPassword());
    }

    @Override
    @Transactional
    public void updateMember(Long memberId, UpdateMemberRequestDto dto) {
        Member member = findMemberById(memberId);

        member.updateMemberInfo(dto.getUsername(), dto.getPhoneNumber(), dto.getAddress(), dto.getDateOfBirthAsLocalDate());

        //새 비밀번호가 입력된 경우에만 비밀번호 변경
        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
            if (!dto.getNewPassword().equalsIgnoreCase(dto.getConfirmNewPassword())) {
                throw new MemberPasswordMismatchException();
            }

            String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
            member.changePassword(encodedPassword);
        }

        memberRepository.save(member);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
    }
}
