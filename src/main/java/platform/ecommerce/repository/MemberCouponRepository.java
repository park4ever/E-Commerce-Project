package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.MemberCoupon;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    List<MemberCoupon> findByMemberId(Long memberId);

    List<MemberCoupon> findByMemberIdAndUsedFalse(Long memberId);

    Optional<MemberCoupon> findByIdAndMemberId(Long memberCouponId, Long memberId);

    boolean existsByMemberIdAndCouponId(Long memberId, Long couponId);
}
