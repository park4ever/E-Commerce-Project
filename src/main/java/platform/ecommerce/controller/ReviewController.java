package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.dto.review.ReviewParametersDto;
import platform.ecommerce.dto.review.ReviewRequestDto;
import platform.ecommerce.dto.review.ReviewResponseDto;
import platform.ecommerce.service.ItemService;
import platform.ecommerce.service.MemberService;
import platform.ecommerce.service.ReviewService;

@Slf4j
@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/new")
    public String createReviewForm(ReviewParametersDto param, Model model) {
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.updateItemId(param.getItemId());
        dto.updateOrderId(param.getOrderId());

        ItemResponseDto item = itemService.findItem(param.getItemId());

        model.addAttribute("reviewRequestDto", dto);
        model.addAttribute("item", item);
        return "/pages/review/reviewForm";
    }

    @PostMapping("/new")
    public String createReview(@Valid ReviewRequestDto dto, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "/pages/review/reviewForm";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        reviewService.writeReview(member.getMemberId(), dto);

        return "redirect:/item/" + dto.getItemOptionId();
    }

    @GetMapping("/{reviewId}")
    public String searchReview(@PathVariable("reviewId") Long reviewId, Model model) {
        ReviewResponseDto review = reviewService.findReview(reviewId);
        model.addAttribute("review", review);
        return "/pages/review/reviewDetails";
    }

    @GetMapping("/edit/{reviewId}")
    public String editReviewForm(@PathVariable("reviewId") Long reviewId, Model model) {
        ReviewResponseDto review = reviewService.findReview(reviewId);
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setReviewId(reviewId);
        dto.setContent(review.getContent());
        dto.setRating(review.getRating());
        dto.setItemOptionId(review.getItemId());
        model.addAttribute("reviewRequestDto", dto);
        return "/pages/review/editReviewForm";
    }

    @PostMapping("/edit/{reviewId}")
    public String updateReview(
            @PathVariable("reviewId") Long reviewId, @Valid ReviewRequestDto reviewRequestDto,
            BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "/pages/review/editReviewForm";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        reviewService.updateReview(reviewId, member.getMemberId(), reviewRequestDto);

        return "redirect:/item/" + reviewRequestDto.getItemOptionId();
    }

    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable("reviewId") Long reviewId) {
        Long itemId = reviewService.deleteReview(reviewId);

        return "redirect:/item/" + itemId;
    }
}