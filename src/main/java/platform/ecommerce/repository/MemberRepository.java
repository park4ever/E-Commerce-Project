package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
