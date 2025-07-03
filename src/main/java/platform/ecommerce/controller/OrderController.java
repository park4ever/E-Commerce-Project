package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.config.auth.LoginMember;
import platform.ecommerce.dto.coupon.MemberCouponResponseDto;
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.dto.member.LoginMemberDto;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.order.*;
import platform.ecommerce.entity.OrderStatus;
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
    private final MemberCouponService memberCouponService;

    @GetMapping("/new")
    public String orderForm(@RequestParam(value = "itemId", required = false) Long itemId,
                            @RequestParam(value = "quantity", required = false) Integer quantity,
                            @RequestParam(value = "fromCart", required = false, defaultValue = "false") boolean fromCart,
                            @LoginMember LoginMemberDto member, Model model) {
        //사용자 정보 가져오기
        MemberDetailsDto memberDetails = memberService.findMemberDetails(member.email());

        OrderSaveRequestDto dto;

        if (fromCart) {
            //장바구니 주문인 경우, prepareOrderFromCart() 호출
            dto = cartService.prepareOrderFromCart(member.id());
        } else {
            //단일 상품 주문인 경우
            ItemResponseDto item = itemService.findItem(itemId);
            model.addAttribute("item", item);
            dto = orderService.buildSingleOrderDto(memberDetails, itemId, quantity);
        }

        List<MemberCouponResponseDto> coupons = memberCouponService.getAllCouponsWithUsability(member.id(), dto.getOrderTotal());

        model.addAttribute("orderSaveRequestDto", dto);
        model.addAttribute("memberDetails", memberDetails);
        model.addAttribute("coupons", coupons);

        return "/pages/order/orderForm";
    }

    @PostMapping("/new")
    public String createOrder(@Valid @ModelAttribute("orderSaveRequestDto") OrderSaveRequestDto dto,
                              BindingResult bindingResult, @LoginMember LoginMemberDto member) {
        if (bindingResult.hasErrors()) {
            log.error("주문 데이터 검증 실패: {}", bindingResult.getAllErrors());
            return "/pages/order/orderForm";
        }

        Long orderId = orderService.processOrder(dto, member.id());

        return "redirect:/order/success?orderId=" + orderId;
    }

    @GetMapping("/my")
    public String findMyOrders(@ModelAttribute("orderPageRequestDto") OrderPageRequestDto dto,
            @LoginMember LoginMemberDto member, Model model) {

        Page<OrderResponseDto> orders = orderService.findMyOrders(member.id(), dto);

        model.addAttribute("orders", orders);
        model.addAttribute("requestDto", dto);

        return "/pages/order/my-orders";
    }

    @GetMapping("/detail/{orderId}")
    public String orderDetails(@PathVariable("orderId") Long orderId,
            @LoginMember LoginMemberDto member, Model model) {
        OrderResponseDto order = orderService.findOrderById(orderId, member.id());
        model.addAttribute("order", order);

        return "/pages/order/orderDetails";
    }

    @GetMapping("/success")
    public String orderSuccess(@RequestParam(value = "orderId", required = false) Long orderId,
                               @LoginMember LoginMemberDto member, Model model) {
        if (orderId != null) {
            OrderResponseDto order = orderService.findOrderById(orderId, member.id());
            model.addAttribute("order", order);
        }
        return "/pages/order/success";
    }

    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam("orderId") Long orderId,
                              @LoginMember LoginMemberDto member) {
        orderService.cancelOrder(orderId, member.id());

        return "redirect:/order/history";
    }

    @GetMapping("/updateShippingAddress/{orderId}")
    public String updateShippingAddressForm(@PathVariable("orderId") Long orderId,
                                            @LoginMember LoginMemberDto member, Model model) {
        OrderResponseDto order = orderService.findOrderById(orderId, member.id());

        OrderModificationDto dto = OrderModificationDto.addressChange(orderId);

        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        model.addAttribute("orderModificationDto", dto);

        return "/pages/order/updateShippingAddressForm";
    }

    @PostMapping("/updateShippingAddress")
    public String updateShippingAddress(@Valid @ModelAttribute("orderModificationDto") OrderModificationDto dto, BindingResult bindingResult,
                                        @LoginMember LoginMemberDto member) {
        if (bindingResult.hasErrors()) {
            log.error("OrderModificationDto binding errors : {}", bindingResult.getAllErrors());
            return "/pages/order/updateShippingAddressForm";
        }

        orderService.updateShippingAddress(dto, member.id());

        return "redirect:/order/detail/" + dto.getOrderId();
    }

    @GetMapping("/requestRefundOrExchange/{orderId}")
    public String requestRefundOrExchangeForm(@PathVariable("orderId") Long orderId,
            @LoginMember LoginMemberDto member, Model model) {
        OrderResponseDto order = orderService.findOrderById(orderId, member.id());

        OrderModificationDto dto = OrderModificationDto.withOrderId(orderId);

        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        model.addAttribute("orderModificationDto", dto);

        return "/pages/order/requestRefundOrExchangeForm";
    }

    @PostMapping("/requestRefundOrExchange")
    public String requestRefundOrExchange(@Valid @ModelAttribute("orderModificationDto") OrderModificationDto dto, BindingResult bindingResult,
                                          @LoginMember LoginMemberDto member) {
        if (bindingResult.hasErrors()) {
            log.error("OrderModificationDto biding errors : {}", bindingResult.getAllErrors());
            return "/pages/order/requestRefundOrExchangeForm";
        }

        orderService.applyRefundOrExchange(dto, member.id());

        return "redirect:/order/detail/" + dto.getOrderId();
    }
}