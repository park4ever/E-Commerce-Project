package platform.ecommerce.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.coupon.MemberCouponIssueRequestDto;
import platform.ecommerce.dto.coupon.MemberCouponResponseDto;
import platform.ecommerce.entity.Coupon;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.MemberCoupon;
import platform.ecommerce.repository.CouponRepository;
import platform.ecommerce.repository.MemberCouponRepository;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.service.MemberCouponService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCouponServiceImpl implements MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long issueCoupon(MemberCouponIssueRequestDto dto) {
        if (memberCouponRepository.existsByMemberIdAndCouponId(dto.getMemberId(), dto.getCouponId())) {
            throw new IllegalStateException("이미 발급된 쿠폰입니다.");
        }

        Coupon coupon = couponRepository.findById(dto.getCouponId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 쿠폰입니다."));

        if (!coupon.isEnabled()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        if (!coupon.isValidNow()) {
            throw new IllegalStateException("유효하지 않는 쿠폰입니다.");
        }

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 유효하지 않습니다."));

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .issuedAt(LocalDateTime.now())
                .used(false)
                .build();
        memberCouponRepository.save(memberCoupon);

        return memberCoupon.getId();
    }

    @Override
    public List<MemberCouponResponseDto> getUsableCoupons(Long memberId, int orderTotal) {
        List<MemberCoupon> all = memberCouponRepository.findByMemberIdAndUsedFalse(memberId);

        return all.stream()
                .filter(mc -> mc.isUsable(orderTotal))
                .map(MemberCouponResponseDto::from)
                .toList();
    }

    @Override
    public List<MemberCouponResponseDto> getAllCoupons(Long memberId) {
        List<MemberCoupon> all = memberCouponRepository.findByMemberId(memberId);

        return all.stream()
                .map(MemberCouponResponseDto::from)
                .toList();
    }

    @Override
    public List<MemberCouponResponseDto> getAllCouponsWithUsability(Long memberId, int orderTotal) {
        List<MemberCoupon> coupons = memberCouponRepository.findByMemberId(memberId);

        return coupons.stream()
                .map(mc -> MemberCouponResponseDto.from(mc, orderTotal))
                .toList();
    }

    @Override
    public MemberCoupon getOwnedCouponOrThrow(Long memberCouponId, Long memberId) {
        return memberCouponRepository.findByIdAndMemberId(memberCouponId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 쿠폰을 사용할 수 없습니다."));
    }

    @Override
    @Transactional
    public void useCoupon(Long memberCouponId, Long memberId) {
        MemberCoupon memberCoupon = getOwnedCouponOrThrow(memberCouponId, memberId);

        if (memberCoupon.isUsed()) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        memberCoupon.markAsUsed();
    }
}
