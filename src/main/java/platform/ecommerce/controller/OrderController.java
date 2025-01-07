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
import platform.ecommerce.dto.cart.CartCheckoutDto;
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.dto.order.*;
import platform.ecommerce.entity.Address;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.Order;
import platform.ecommerce.entity.OrderStatus;
import platform.ecommerce.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public String orderForm(@RequestParam(value = "itemId", required = false) Long itemId,
                            @RequestParam(value = "quantity", required = false) Integer quantity,
                            Model model, Authentication authentication) {
        log.info("itemId : {}, quantity : {}", itemId, quantity);
        //상품 정보 모델에 추가
        ItemResponseDto item = itemService.findItem(itemId);
        model.addAttribute("item", item);

        log.info("item.id : {}, item.itemName = {}", item.getId(), item.getItemName());

        //사용자 정보 불러오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());
        MemberDetailsDto memberDetails = memberService.findMemberDetails(userDetails.getUsername());

        //주문 생성
        OrderSaveRequestDto orderSaveRequestDto = orderService.createOrderSaveRequestDto(memberDetails, itemId, quantity);
        orderSaveRequestDto.setMemberId(member.getMemberId());
        model.addAttribute("orderSaveRequestDto", orderSaveRequestDto);

        log.info("OrderSaveRequestDto created : {}", orderSaveRequestDto);

        return "/pages/order/orderForm";
    }

    @PostMapping("/new")
    public String createOrder(@Valid @ModelAttribute("orderSaveRequestDto") OrderSaveRequestDto orderSaveRequestDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.error("OrderSaveRequestDto binding errors : {}", bindingResult.getAllErrors());
            return "/pages/order/orderForm";
        }

        log.info("OrderSaveRequestDto = {}", orderSaveRequestDto);
        Long orderId = orderService.createOrder(orderSaveRequestDto);
        return "redirect:/order/success";
    }

    @GetMapping("/history")
    public String findOrders(
            @ModelAttribute("searchCondition") OrderSearchCondition searchCondition, Pageable pageable,
            Model model, Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        Page<OrderResponseDto> orders = orderService.findOrdersWithPageable(searchCondition, member.getMemberId(), pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("searchCondition", searchCondition);
        return "/pages/order/orderHistory";
    }

    @GetMapping("/detail/{orderId}")
    public String orderDetails(@PathVariable("orderId") Long orderId, Model model) {
        OrderResponseDto order = orderService.findOrderById(orderId);
        model.addAttribute("order", order);
        return "/pages/order/orderDetails";
    }

    @GetMapping("/checkout")
    public String checkoutForm(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        CartCheckoutDto cartCheckoutDto = cartService.prepareCheckout(member.getMemberId(), new CartCheckoutDto());
        model.addAttribute("cartCheckoutDto", cartCheckoutDto);
        return "/pages/order/checkout";
    }

    @PostMapping("/checkout")
    public String checkoutCart(
            @ModelAttribute("cartCheckoutDto") CartCheckoutDto cartCheckoutDto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        //memberId 설정
        cartCheckoutDto.setMemberId(member.getMemberId());
        log.info("CartChekckoutDto before checkout : {}", cartCheckoutDto);
        //사용자 제공 데이터를 포함한 DTO를 서비스로 전달
        checkoutService.checkoutCart(cartCheckoutDto);
        return "redirect:/order/success";
    }

    @GetMapping("/success")
    public String orderSuccess() {
        return "/pages/order/success";
    }

    @PostMapping("/updateStatus")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("status") OrderStatus status) {
        log.info("Updating order status : orderId = {}, status = {}", orderId, status);
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/order/history";
    }

    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam("orderId") Long orderId) {
        log.info("Cancelling order : orderId = {}", orderId);
        orderService.cancelOrder(orderId);
        return "redirect:/order/history";
    }

    @GetMapping("/updateShippingAddress/{orderId}")
    public String updateShippingAddressForm(@PathVariable("orderId") Long orderId, Model model) {
        OrderResponseDto order = orderService.findOrderById(orderId);

        OrderModificationDto dto = new OrderModificationDto();
        dto.setOrderId(orderId);

        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        model.addAttribute("orderModificationDto", dto);
        return "/pages/order/updateShippingAddressForm";
    }

    @PostMapping("/updateShippingAddress")
    public String updateShippingAddress(@Valid @ModelAttribute("orderModificationDto") OrderModificationDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("OrderModificationDto binding errors : {}", bindingResult.getAllErrors());
            return "/pages/order/updateShippingAddressForm";
        }

        orderService.updateShippingAddress(dto);
        return "redirect:/order/detail/" + dto.getOrderId();
    }

    @GetMapping("/requestRefundOrExchange/{orderId}")
    public String requestRefundOrExchangeForm(@PathVariable("orderId") Long orderId, Model model) {
        OrderResponseDto order = orderService.findOrderById(orderId);

        OrderModificationDto dto = new OrderModificationDto();
        dto.setOrderId(orderId);

        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        model.addAttribute("orderModificationDto", dto);
        return "/pages/order/requestRefundOrExchangeForm";
    }

    @PostMapping("/requestRefundOrExchange")
    public String requestRefundOrExchange(@Valid @ModelAttribute("orderModificationDto") OrderModificationDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("OrderModificationDto biding errors : {}", bindingResult.getAllErrors());
            return "/pages/order/requestRefundOrExchangeForm";
        }

        orderService.requestRefundOrExchange(dto);
        return "redirect:/order/detail/" + dto.getOrderId();
    }
}
