package platform.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import platform.ecommerce.dto.admin.AdminItemDto;
import platform.ecommerce.dto.admin.AdminMemberDto;
import platform.ecommerce.dto.admin.AdminOrderDto;
import platform.ecommerce.dto.admin.AdminReviewDto;
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
    public String getAllMembers(@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                Pageable pageable, Model model) {
        Page<AdminMemberDto> members = adminService.getAllMembers(searchKeyword, pageable);
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

        return "pages/admin/memberDetail";
    }

    /**
     * 상품 목록 조회
     */
    @GetMapping("/items")
    public String getAllItems(@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                              Pageable pageable, Model model) {
        Page<AdminItemDto> items = adminService.getAllItems(searchKeyword, pageable);
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

        return "pages/admin/itemDetail";
    }

    /**
     * 주문 목록 조회
     */
    @GetMapping("/orders")
    public String getAllOrders(@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                               Pageable pageable, Model model) {
        Page<AdminOrderDto> orders = adminService.getAllOrders(searchKeyword, pageable);
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

        return "pages/admin/orderDetail";
    }

    /**
     * 리뷰 목록 조회
     */
    @GetMapping("/reviews")
    public String getAllReviews(@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                Pageable pageable, Model model) {
        Page<AdminReviewDto> reviews = adminService.getAllReviews(searchKeyword, pageable);
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

        return "pages/admin/reviewDetail";
    }
}
