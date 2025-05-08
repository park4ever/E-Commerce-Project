package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.item.*;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.review.ReviewPageRequestDto;
import platform.ecommerce.dto.review.ReviewResponseDto;
import platform.ecommerce.service.ItemService;
import platform.ecommerce.service.MemberService;
import platform.ecommerce.service.ReviewService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;
    private final MemberService memberService;
    private final ReviewService reviewService;

    @GetMapping("/new")
    public String itemForm(Model model) {
        model.addAttribute("title", "상품 등록");
        model.addAttribute("itemSaveRequestDto", new ItemSaveRequestDto());
        return "/pages/item/item-create";
    }

    @PostMapping("/new")
    public String registration(@Valid @ModelAttribute("itemSaveRequestDto") ItemSaveRequestDto itemSaveRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/pages/item/itemForm";
        }
        itemService.saveItem(itemSaveRequestDto);

        return "redirect:/item/list";
    }

    @GetMapping("/list")
    public String itemList(@ModelAttribute("itemPageRequestDto") ItemPageRequestDto requestDto,
                           Pageable pageable, Model model) {
        model.addAttribute("title", "상품 목록");
        Page<ItemResponseDto> items = itemService.findItemsWithPageable(requestDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemPageRequestDto", requestDto);

        return "/pages/item/item-list";
    }

    @GetMapping("/{itemId}")
    public String viewItem(@PathVariable("itemId") Long itemId,
                           @ModelAttribute("reviewPageRequestDto") ReviewPageRequestDto requestDto, Pageable pageable,
                           Model model, Authentication authentication) {
        model.addAttribute("title", "상품 상세 정보");

        //기본 정보 조회
        ItemResponseDto item = itemService.findItem(itemId);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDetailsDto member = memberService.findMemberDetails(userDetails.getUsername());

        //리뷰 관련 데이터
        Page<ReviewResponseDto> reviews = reviewService.searchReviews(requestDto, pageable);
        double ratingAvg = reviewService.calculateAverageRating(itemId);
        long reviewCount = reviewService.countReviewsByItemId(itemId);

        //ViewModel 생성 및 모델에 추가
        ItemDetailViewModel viewModel = ItemDetailViewModel.of(item, member, reviews, ratingAvg, reviewCount);
        model.addAttribute("viewModel", viewModel);

        //리뷰 조건 다시 넣어줌(페이징 등 이슈)
        model.addAttribute("reviewPageRequestDto", requestDto);

        return "/pages/item/item-detail";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("title", "상품 수정");
        ItemResponseDto itemResponseDto = itemService.findItem(id);
        ItemUpdateDto itemUpdateDto = itemService.convertToUpdateDto(itemResponseDto);
        model.addAttribute("item", itemUpdateDto);
        model.addAttribute("id", id); // ID를 모델에 추가하여 폼에 전달

        return "/pages/item/item-edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("item") ItemUpdateDto itemUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/pages/item/item-edit";
        }

        itemService.updateItem(id, itemUpdateDto);

        return "redirect:/item/list";
    }
}