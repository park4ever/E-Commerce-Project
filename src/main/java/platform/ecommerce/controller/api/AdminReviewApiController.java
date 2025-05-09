package platform.ecommerce.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.admin.AdminReviewDto;
import platform.ecommerce.dto.review.ReviewPageRequestDto;
import platform.ecommerce.service.AdminService;

import static org.springframework.data.domain.Sort.Direction.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reviews")
public class AdminReviewApiController {

    private final AdminService adminService;

    /**
     * 리뷰 목록 조회(검색 및 페이징 포함)
     */
    @GetMapping
    public ResponseEntity<Page<AdminReviewDto>> getAllReviews(ReviewPageRequestDto requestDto) {
        Page<AdminReviewDto> reviews = adminService.getAllReviews(requestDto);

        return ResponseEntity.ok(reviews);
    }

    /**
     * 리뷰 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminReviewDto> getReviewById(@PathVariable("id") Long id) {
        AdminReviewDto review = adminService.getReviewById(id);

        return ResponseEntity.ok(review);
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Long id) {
        adminService.deleteReview(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * 리뷰 공개/비공개 설정
     */
    @PutMapping("/{id}/visibility")
    public ResponseEntity<Void> toggleReviewVisibility(@PathVariable("id") Long id,
                                                       @RequestParam("isVisible") boolean isVisible) {
        adminService.toggleReviewVisibility(id, isVisible);

        return ResponseEntity.noContent().build();
    }

    /**
     * 관리자 답변 추가
     */
    @PostMapping("/{id}/reply")
    public ResponseEntity<Void> addAdminReply(@PathVariable("id") Long id,
                                              @RequestBody String reply) {
        adminService.addAdminReply(id, reply);

        return ResponseEntity.noContent().build();
    }

    /**
     * 관리자 답변 삭제
     */
    @DeleteMapping("/{id}/reply")
    public ResponseEntity<Void> removeAdminReply(@PathVariable("id") Long id) {
        adminService.removeAdminReply(id);

        return ResponseEntity.noContent().build();
    }
}
