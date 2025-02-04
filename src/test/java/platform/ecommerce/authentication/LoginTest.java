package platform.ecommerce.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.Role;
import platform.ecommerce.repository.MemberRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;



@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        //테스트용 회원 가입
        Member member = Member.builder()
                .email("tester@test.com")
                .password(passwordEncoder.encode("1234!"))
                .role(Role.USER)
                .phoneNumber("01012345678")
                .build();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("로그인 성공 시, 홈으로 redirect")
    void loginSuccessRedirectTest() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("email", "tester@test.com")
                        .password("password", "1234!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("로그인 실패 시, 로그인 페이지 유지")
    void loginFailureTest() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("email", "wrong@test.com")
                        .password("password", "wrongPw"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @DisplayName("인증 없이 /home 접근 시, 로그인 페이지로 리다이렉트")
    void accessHomeWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("**/login"));
    }
}
