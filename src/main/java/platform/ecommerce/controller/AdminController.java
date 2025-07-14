package platform.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import platform.ecommerce.dto.admin.AdminItemDto;
import platform.ecommerce.dto.admin.AdminMemberDto;
import platform.ecommerce.dto.admin.AdminOrderDto;
import platform.ecommerce.dto.admin.AdminReviewDto;
import platform.ecommerce.dto.item.ItemPageRequestDto;
import platform.ecommerce.dto.member.MemberPageRequestDto;
import platform.ecommerce.dto.order.OrderPageRequestDto;
import platform.ecommerce.dto.review.ReviewPageRequestDto;
import platform.ecommerce.service.AdminService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * 관리자 메인 페이지
     */
    @GetMapping
    public String adminDashboard() {
        return "pages/admin/dashboard";
    }

    /**
     * 회원 목록 조회
     */
    @GetMapping("/members")
    public String getAllMembers(MemberPageRequestDto requestDto, Model model) {
        Page<AdminMemberDto> members = adminService.getAllMembers(requestDto);
        model.addAttribute("members", members);

        return "pages/admin/members";
    }

    /**
     * 회원 상세 조회
     */
    @GetMapping("/members/{id}")
    public String getMemberDetail(@PathVariable("id") Long id, Model model) {
        AdminMemberDto member = adminService.getMemberById(id);
        model.addAttribute("member", member);

        return "pages/admin/member-detail";
    }

    /**
     * 상품 목록 조회
     */
    @GetMapping("/items")
    public String getAllItems(ItemPageRequestDto requestDto, Model model) {
        Page<AdminItemDto> items = adminService.getAllItems(requestDto);
        model.addAttribute("items", items);

        return "pages/admin/items";
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/items/{id}")
    public String getItemDetail(@PathVariable("id") Long id, Model model) {
        AdminItemDto item = adminService.getItemById(id);
        model.addAttribute("item", item);

        return "pages/admin/admin-item-detail";
    }

    /**
     * 주문 목록 조회
     */
    @GetMapping("/orders")
    public String getAllOrders(OrderPageRequestDto requestDto, Model model) {
        Page<AdminOrderDto> orders = adminService.getAllOrders(requestDto);
        model.addAttribute("orders", orders);

        return "pages/admin/orders";
    }

    /**
     * 주문 상세 조회
     */
    @GetMapping("/orders/{id}")
    public String getOrderDetail(@PathVariable("id") Long id, Model model) {
        AdminOrderDto order = adminService.getOrderById(id);
        model.addAttribute("order", order);

        return "pages/admin/admin-order-detail";
    }

    /**
     * 리뷰 목록 조회
     */
    @GetMapping("/reviews")
    public String getAllReviews(ReviewPageRequestDto requestDto, Model model) {
        Page<AdminReviewDto> reviews = adminService.getAllReviews(requestDto);
        model.addAttribute("reviews", reviews);

        return "pages/admin/reviews";
    }

    /**
     * 리뷰 상세 조회
     */
    @GetMapping("/reviews/{id}")
    public String getReviewDetail(@PathVariable("id") Long id, Model model) {
        AdminReviewDto review = adminService.getReviewById(id);
        model.addAttribute("review", review);

        return "pages/admin/admin-review-detail";
    }
}