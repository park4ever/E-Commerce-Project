package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.review.*;
import platform.ecommerce.entity.*;
import platform.ecommerce.exception.file.FileDeleteFailedException;
import platform.ecommerce.exception.item.ItemOptionNotFoundException;
import platform.ecommerce.exception.member.MemberNotFoundException;
import platform.ecommerce.exception.order.OrderItemNotFoundException;
import platform.ecommerce.exception.review.ReviewForbiddenException;
import platform.ecommerce.exception.review.ReviewNotFoundException;
import platform.ecommerce.repository.*;
import platform.ecommerce.service.ReviewService;
import platform.ecommerce.service.upload.FileStorageService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static platform.ecommerce.entity.OrderStatus.*;

@Slf4j
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

        Review review = Review.createReview(item, member, dto.getContent(), dto.getRating());
        item.addReview(review);

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
        Review review = findReviewById(reviewId);

        return ReviewResponseDto.from(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> searchReviewsForItem(ReviewQueryDto queryDto) {
        return reviewRepository.searchReviewsForItem(queryDto)
                .map(ReviewResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> searchMyReviews(Long memberId, MyReviewQueryDto queryDto) {
        Page<Review> reviews = reviewRepository.searchMyReviews(memberId, queryDto);

        return reviews.map(ReviewResponseDto::from);
    }

    @Override
    public ReviewResponseDto updateReview(Long reviewId, Long memberId, ReviewRequestDto dto) {
        Review review = findReviewById(reviewId);

        validateOwnership(review, memberId);

        review.updateReview(dto.getContent(), dto.getRating());

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            //기존에 이미지가 있다면 삭제
            if (review.getImageUrl() != null) {
                try {
                    fileStorageService.delete(review.getImageUrl());
                } catch (FileDeleteFailedException e) {
                    log.warn("리뷰 이미지 삭제 실패 : {}", review.getImageUrl(), e);
                }
            }

            String imageUrl = fileStorageService.store(dto.getImage(), "review");
            review.updateImageUrl(imageUrl);
        }

        return ReviewResponseDto.from(review);
    }

    @Override
    public Long deleteReview(Long reviewId, Long memberId) {
        Review review = findReviewById(reviewId);

        validateOwnership(review, memberId);

        if (review.getImageUrl() != null) {
            try {
                fileStorageService.delete(review.getImageUrl());
            } catch (FileDeleteFailedException e) {
                log.warn("리뷰 이미지 삭제 실패 : {}", review.getImageUrl(), e);
            }
        }
        review.getItem().removeReview(review);
        reviewRepository.delete(review);

        return review.getItem().getId();
    }

    @Override
    @Transactional(readOnly = true)
    public double getAverageRating(Long itemId) {
        Double avg = reviewRepository.getAverageRatingByItemId(itemId);

        return Optional.ofNullable(avg).orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Double> getAverageRatingMap(List<Long> itemIds) {
        return reviewRepository.getAverageRatingMapByItemIds(itemIds);
    }

    @Override
    @Transactional(readOnly = true)
    public long countReviewsByItemId(Long itemId) {
        return reviewRepository.countByItemId(itemId);
    }

    private void validateOwnership(Review review, Long memberId) {
        if (!memberId.equals(review.getMember().getId())) {
            throw new ReviewForbiddenException();
        }
    }

    private Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
    }
}