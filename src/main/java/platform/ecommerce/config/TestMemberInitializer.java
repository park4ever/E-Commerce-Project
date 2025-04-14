package platform.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import platform.ecommerce.entity.Address;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.Role;
import platform.ecommerce.repository.MemberRepository;

import java.time.LocalDate;

//@Configuration
@RequiredArgsConstructor
public class TestMemberInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.count() > 10) {
            System.out.println("⚠️ 이미 회원이 10명 이상 있어, 테스트 데이터 생성을 건너뜁니다. 현재 회원 수 : " + memberRepository.count());
            return;
        }

        Address fixedAddress = new Address(
                "서울",
                "중구 을지로 100",
                "04524",
                "101동 101호"
        );

        String[] lastNames = {"김", "이", "박", "최", "정"};

        for (int i = 1; i <= 50; i++) {
            String lastName = lastNames[(i - 1) / 10];

            Member member = Member.builder()
                    .email("tester" + i + "@example.com")
                    .username(lastName + "테스" + i)
                    .password(passwordEncoder.encode("1234!"))
                    .phoneNumber("0101234" + String.format("%04d", i))
                    .address(fixedAddress)
                    .dateOfBirth(LocalDate.of(1990, 1, (i % 28) + 1))
                    .role(Role.USER)
                    .isActive(true)
                    .build();

            memberRepository.save(member);
        }

        System.out.println("✅ 테스트 회원 50명 생성 완료!");
    }
}
