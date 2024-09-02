package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import platform.ecommerce.dto.*;
import platform.ecommerce.service.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;
    private final CartService cartService;
    private final CheckoutService checkoutService;

    @GetMapping("/new")
    public String orderForm(Model model) {
        List<ItemResponseDto> items = itemService.findItems();
        items.forEach(item -> System.out.println("item.getItemName = " + item.getItemName()));
        model.addAttribute("items", items);
        model.addAttribute("orderSaveRequestDto", new OrderSaveRequestDto());
        return "/pages/order/orderForm";
    }

    @PostMapping("/new")
    public String createOrder(@Valid OrderSaveRequestDto orderSaveRequestDto, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            log.info("FORM VALIDATION ERRORS : {}", bindingResult.getAllErrors());
            return "/pages/order/orderForm";
        }

        log.info("orderSaveRequestDto = {}", orderSaveRequestDto);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        //DTO에 로그인한 사용자 ID 설정
        orderSaveRequestDto.setMemberId(member.getMemberId());

        try {
            orderService.createOrder(orderSaveRequestDto);
            log.info("ORDER SUCCESSFULLY CREATED");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("ORDER CREATED FAILED");
            return "/pages/order/orderForm";
        }

        return "redirect:/";
    }

    @GetMapping("/history")
    public String findOrders(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        List<OrderResponseDto> orders = orderService.findOrdersByMemberId(member.getMemberId());

        model.addAttribute("orders", orders);

        return "/pages/order/orderHistory";
    }

    @GetMapping("/checkout")
    public String checkoutForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        CartCheckoutDto cartCheckoutDto = cartService.prepareCheckout(member.getMemberId());
        model.addAttribute("cartCheckoutDto", cartCheckoutDto);
        return "/pages/order/checkout";
    }

    @PostMapping("/checkout")
    public String checkoutCart(
            @ModelAttribute("cartCheckoutDto") CartCheckoutDto cartCheckoutDto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        checkoutService.checkoutCart(member.getMemberId());
        return "redirect:/order/success";
    }

    @GetMapping("/success")
    public String orderSuccess() {
        return "/pages/order/success";
    }
}
