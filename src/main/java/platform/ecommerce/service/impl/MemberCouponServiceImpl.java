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
import platform.ecommerce.exception.coupon.*;
import platform.ecommerce.exception.member.MemberNotFoundException;
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
            throw new CouponDuplicateIssueException();
        }

        Coupon coupon = couponRepository.findById(dto.getCouponId())
                .orElseThrow(CouponNotFoundException::new);

        if (!coupon.isEnabled()) {
            throw new CouponNotUsableException();
        }

        if (!coupon.isValidNow()) {
            throw new InvalidCouponException();
        }

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

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
                .orElseThrow(CouponNotUsableException::new);
    }

    @Override
    @Transactional
    public void useCoupon(Long memberCouponId, Long memberId) {
        MemberCoupon memberCoupon = getOwnedCouponOrThrow(memberCouponId, memberId);

        if (memberCoupon.isUsed()) {
            throw new CouponAlreadyUsedException();
        }

        memberCoupon.markAsUsed();
    }
}
