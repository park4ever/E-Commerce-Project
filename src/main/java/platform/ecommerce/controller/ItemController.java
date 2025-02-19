package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.dto.item.ItemSaveRequestDto;
import platform.ecommerce.dto.item.ItemSearchCondition;
import platform.ecommerce.dto.item.ItemUpdateDto;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.dto.review.ReviewResponseDto;
import platform.ecommerce.dto.review.ReviewSearchCondition;
import platform.ecommerce.service.ItemService;
import platform.ecommerce.service.MemberService;
import platform.ecommerce.service.ReviewService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;
    private final MemberService memberService;
    private final ReviewService reviewService;

    @GetMapping("/new")
    public String itemForm(Model model) {
        model.addAttribute("itemSaveRequestDto", new ItemSaveRequestDto());
        return "/pages/item/itemForm";
    }

    @PostMapping("/new")
    public String registration(@Valid ItemSaveRequestDto itemSaveRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/pages/item/itemForm";
        }
        itemService.saveItem(itemSaveRequestDto);

        return "redirect:/item/list";
    }

    @GetMapping("/list")
    public String itemList(ItemSearchCondition cond, Pageable pageable, Model model) {
        Page<ItemResponseDto> items = itemService.findItemsWithPageable(cond, pageable);
        model.addAttribute("items", items);
        model.addAttribute("searchCondition", cond);

        return "/pages/item/itemList";
    }

    @GetMapping("/{itemId}")
    public String viewItem(@PathVariable("itemId") Long itemId,
                           @ModelAttribute("reviewSearchCondition") ReviewSearchCondition cond, Pageable pageable,
                           Model model, Authentication authentication) {
        //상품 정보 조회
        ItemResponseDto item = itemService.findItem(itemId);

        //사용자 정보 조회
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDetailsDto memberDetails = memberService.findMemberDetails(userDetails.getUsername());
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());
        model.addAttribute("member", member);

        //리뷰 추가 + 필터링
        cond.updateItemId(itemId);
        Page<ReviewResponseDto> reviews = reviewService.findReviewsWithPageable(cond, pageable);
        model.addAttribute("reviews", reviews);

        //별점 평균
        double ratingAvg = reviewService.calculateAverageRating(itemId);
        model.addAttribute("ratingAverage", ratingAvg);

        //리뷰 개수 합산
        long reviewCount = reviewService.countReviewsByItemId(itemId);
        model.addAttribute("reviewCount", reviewCount);

        //모델에 기본 정보 추가
        model.addAttribute("item", item);
        model.addAttribute("defaultQuantity", 1); //기본 수량
        model.addAttribute("customerName", memberDetails.getUsername());
        model.addAttribute("customerPhone", memberDetails.getPhoneNumber());
        //TODO getAddress() -> getFullAddress() 수정했는데, 추후 문제있을 경우 확인!
        model.addAttribute("customerAddress", memberDetails.getFullAddress());

        return "/pages/item/itemDetail";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        ItemResponseDto itemResponseDto = itemService.findItem(id);
        ItemUpdateDto itemUpdateDto = itemService.convertToUpdateDto(itemResponseDto);
        model.addAttribute("item", itemUpdateDto);
        model.addAttribute("id", id); // ID를 모델에 추가하여 폼에 전달

        return "/pages/item/itemEditForm";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("item") ItemUpdateDto itemUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/pages/item/itemEditForm";
        }

        itemService.updateItem(id, itemUpdateDto);

        return "redirect:/item/list";
    }
}
