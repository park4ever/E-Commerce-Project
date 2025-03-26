package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.Member;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByEmail(String email);

    long countByCreatedDateAfter(LocalDateTime dateTime);
}
