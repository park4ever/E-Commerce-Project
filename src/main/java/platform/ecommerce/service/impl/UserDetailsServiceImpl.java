package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.member.MemberDto;
import platform.ecommerce.entity.Member;
import platform.ecommerce.exception.member.MemberNotFoundException;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

//        return toUserDetails(member);
        MemberDto memberDto = new MemberDto(member);
        return new CustomUserDetails(memberDto);
    }

    private UserDetails toUserDetails(Member member) {
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()))
                .build();
    }
}
