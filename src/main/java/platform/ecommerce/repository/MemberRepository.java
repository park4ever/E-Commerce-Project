package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import platform.ecommerce.entity.Member;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.email LIKE %:keyword% OR m.username LIKE %:keyword%")
    Page<Member> searchMembers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Member m")
    Page<Member> findAllMembers(Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.id = :memberId AND m.isActive = true")
    Optional<Member> findActiveMemberById(Long memberId);

    long countByCreatedDateAfter(LocalDateTime dateTime);
}
