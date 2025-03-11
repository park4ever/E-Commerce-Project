package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import platform.ecommerce.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE (m.email LIKE %:keyword% OR m.username LIKE %:keyword%) AND m.isActive = true")
    Page<Member> searchActiveMembers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.isActive = true")
    Page<Member> findAllActiveMembers(Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.id = :memberId AND m.isActive = true")
    Optional<Member> findActiveMemberById(Long memberId);
}
