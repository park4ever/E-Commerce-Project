package platform.ecommerce.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.review.ReviewPageRequestDto;
import platform.ecommerce.dto.review.ReviewRequestDto;
import platform.ecommerce.dto.review.ReviewResponseDto;
import platform.ecommerce.entity.*;
import platform.ecommerce.exception.item.ItemOptionNotFoundException;
import platform.ecommerce.exception.member.MemberNotFoundException;
import platform.ecommerce.exception.order.OrderItemNotFoundException;
import platform.ecommerce.exception.review.ReviewForbiddenException;
import platform.ecommerce.exception.review.ReviewNotFoundException;
import platform.ecommerce.repository.*;
import platform.ecommerce.service.ReviewService;
import platform.ecommerce.service.upload.FileStorageService;

import java.util.List;
import java.util.stream.Collectors;

import static platform.ecommerce.entity.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Long writeReview(Long memberId, ReviewRequestDto dto) {
        ItemOption option = itemOptionRepository.findById(dto.getItemOptionId())
                .orElseThrow(ItemOptionNotFoundException::new);
        Item item = option.getItem();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        //createReview 메서드로 리뷰 생성
        Review review = Review.createReview(item, member, dto.getContent(), dto.getRating());

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String imageUrl = fileStorageService.store(dto.getImage(), "review");
            review.updateImageUrl(imageUrl);
        }

        OrderItem orderItem = orderItemRepository.findByOrderIdAndItemOptionId(dto.getOrderId(), dto.getItemOptionId())
                .orElseThrow(OrderItemNotFoundException::new);
        orderItem.getOrder().updateStatus(COMPLETED);

        return reviewRepository.save(review).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto findReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        return toDto(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsForItem(Long itemId) {
        List<Review> reviews = reviewRepository.findByItemId(itemId);
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> searchReviews(ReviewPageRequestDto requestDto, Pageable pageable) {
        Page<Review> sortedReviews = reviewRepository.searchReviews(requestDto, pageable);

        return sortedReviews.map(this::toDto);
    }

    @Override
    public ReviewResponseDto updateReview(Long reviewId, Long memberId, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        if (!memberId.equals(review.getMember().getId())) {
            throw new ReviewForbiddenException();
        }

        //리뷰 내용과 평점 업데이트
        review.updateReview(dto.getContent(), dto.getRating());

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String imageUrl = fileStorageService.store(dto.getImage(), "review");
            review.updateImageUrl(imageUrl);
        }

        return toDto(review);
    }

    @Override
    public Long deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        Long itemId = review.getItem().getId();
        reviewRepository.delete(review);

        return itemId;
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateAverageRating(Long itemId) {
        List<Review> reviews = reviewRepository.findByItemId(itemId);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countReviewsByItemId(Long itemId) {
        return reviewRepository.countByItemId(itemId);
    }

    private ReviewResponseDto toDto(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getId())
                .itemId(review.getItem().getId())
                .memberId(review.getMember().getId())
                .memberName(review.getMember().getUsername())
                .content(review.getContent())
                .rating(review.getRating())
                .imageUrl(review.getImageUrl())
                .build();
    }
}