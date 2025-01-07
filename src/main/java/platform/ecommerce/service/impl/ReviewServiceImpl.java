package platform.ecommerce.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import platform.ecommerce.dto.review.ReviewRequestDto;
import platform.ecommerce.dto.review.ReviewResponseDto;
import platform.ecommerce.dto.review.ReviewSearchCondition;
import platform.ecommerce.entity.*;
import platform.ecommerce.repository.*;
import platform.ecommerce.service.ReviewService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static platform.ecommerce.entity.OrderStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public Long createReview(Long memberId, ReviewRequestDto dto) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        //TODO erase
        /*Review review = Review.builder()
                .item(item)
                .member(member)
                .content(dto.getContent())
                .rating(dto.getRating())
                .build();*/

        //createReview 메서드로 리뷰 생성
        Review review = Review.createReview(item, member, dto.getContent(), dto.getRating());

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String imageUrl = saveImage(dto.getImage());
            review.updateImageUrl(imageUrl);
        }

        OrderItem orderItem = orderItemRepository.findByOrderIdAndItemId(dto.getOrderId(), dto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("주문 상품을 찾을 수 없습니다."));
        orderItem.getOrder().updateStatus(COMPLETED);
        orderItemRepository.save(orderItem);

        return reviewRepository.save(review).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto findReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        return ReviewResponseDto.builder()
                .reviewId(review.getId())
                .itemId(review.getItem().getId())
                .memberId(review.getMember().getId())
                .memberName(review.getMember().getUsername())
                .content(review.getContent())
                .rating(review.getRating())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> findReviewsByItemId(Long itemId) {
        List<Review> reviews = reviewRepository.findByItemId(itemId);
        return reviews.stream()
                .map(review -> ReviewResponseDto.builder()
                        .reviewId(review.getId())
                        .itemId(review.getItem().getId())
                        .memberId(review.getMember().getId())
                        .memberName(review.getMember().getUsername())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .imageUrl("/images/review/" + review.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> findReviewsWithPageable(ReviewSearchCondition cond, Pageable pageable) {
        Page<Review> sortedReviews = reviewRepository.findReviewsWithPageable(cond, pageable);

        return sortedReviews.map(review -> ReviewResponseDto.builder()
                        .reviewId(review.getId())
                        .itemId(review.getItem().getId())
                        .memberId(review.getMember().getId())
                        .memberName(review.getMember().getUsername())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .imageUrl("/images/review/" + review.getImageUrl())
                        .build());
    }

    @Override
    public ReviewResponseDto updateReview(Long reviewId, Long memberId, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        if (!memberId.equals(review.getMember().getId())) {
            throw new IllegalArgumentException("다른 회원이 작성한 리뷰는 수정할 수 없습니다.");
        }

        //리뷰 내용과 평점 업데이트
        review.updateReview(dto.getContent(), dto.getRating());

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String imageUrl = saveImage(dto.getImage());
            review.updateImageUrl(imageUrl);
        }

        return ReviewResponseDto.builder()
                .reviewId(review.getId())
                .itemId(review.getItem().getId())
                .memberId(memberId)
                .memberName(review.getMember().getUsername())
                .content(dto.getContent())
                .rating(dto.getRating())
                .imageUrl("/images/review/" + review.getImageUrl())
                .build();
    }

    @Override
    public Long deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

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

    private String saveImage(MultipartFile imageFile) {
        try {
            //UUID와 원래 이미지 파일 이름을 결합한 파일명 생성
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            //파일 경로 저장
            Path filePath = Paths.get(uploadDir + "/review", fileName);
            //이미지 파일을 저장된 경로에 저장
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName; //파일명을 반환
        } catch (IOException e) {
            throw new RuntimeException("리뷰 이미지 저장에 실패하였습니다.", e);
        }
    }
}