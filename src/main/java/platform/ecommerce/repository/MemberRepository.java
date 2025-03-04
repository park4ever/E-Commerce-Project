package platform.ecommerce.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import platform.ecommerce.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE (m.email LIKE %:keyword% OR m.username LIKE %:keyword%) AND m.isActive = true")
    List<Member> searchActiveMembers(String email, String username, Sort sort);

    @Query("SELECT m FROM Member m WHERE m.isActive = true")
    List<Member> findAllActiveMembers(Sort sort);

    @Query("SELECT m FROM Member m WHERE m.id = :memberId AND m.isActive = true")
    Optional<Member> findActiveMemberById(Long memberId);
}
