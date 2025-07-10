package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.config.auth.LoginMember;
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.dto.member.LoginMemberDto;
import platform.ecommerce.dto.review.ReviewRequestDto;
import platform.ecommerce.dto.review.ReviewResponseDto;
import platform.ecommerce.service.ItemService;
import platform.ecommerce.service.ReviewService;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ItemService itemService;

    @GetMapping("/new")
    public String createReviewForm(@RequestParam("itemOptionId") Long itemOptionId,
                                   @RequestParam("orderId") Long orderId,
                                   @RequestParam("itemId") Long itemId , Model model) {
        ReviewRequestDto dto = ReviewRequestDto.forForm(itemOptionId, orderId);
        ItemResponseDto item = itemService.findItem(itemId);

        model.addAttribute("reviewRequestDto", dto);
        model.addAttribute("item", item);

        return "/pages/review/reviewForm";
    }

    @PostMapping("/new")
    public String createReview(@Valid ReviewRequestDto dto, BindingResult bindingResult,
                               @LoginMember LoginMemberDto member, Model model) {
        if (bindingResult.hasErrors()) {
            ItemResponseDto item = itemService.findItem(dto.getItemOptionId());
            model.addAttribute("item", item);
            return "/pages/review/reviewForm";
        }
        reviewService.writeReview(member.id(), dto);

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
        ReviewRequestDto dto = ReviewRequestDto.from(review);
        ItemResponseDto item = itemService.findItem(review.getItemId());

        model.addAttribute("item", item);
        model.addAttribute("reviewRequestDto", dto);

        return "/pages/review/editReviewForm";
    }

    @PostMapping("/edit/{reviewId}")
    public String updateReview(@PathVariable("reviewId") Long reviewId,
                               @Valid ReviewRequestDto dto, BindingResult bindingResult,
                               @LoginMember LoginMemberDto member, Model model) {
        if (bindingResult.hasErrors()) {
            ReviewResponseDto review = reviewService.findReview(reviewId);
            model.addAttribute("reviewRequestDto", dto);
            model.addAttribute("item", itemService.findItem(review.getItemId()));
            return "/pages/review/editReviewForm";
        }
        reviewService.updateReview(reviewId, member.id(), dto);

        return "redirect:/item/" + dto.getItemOptionId();
    }

    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable("reviewId") Long reviewId,
                               @LoginMember LoginMemberDto member) {
        Long itemId = reviewService.deleteReview(reviewId, member.id());

        return "redirect:/item/" + itemId;
    }
}