package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.coupon.MemberCouponResponseDto;
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.dto.order.*;
import platform.ecommerce.entity.MemberCoupon;
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
                            Model model, Authentication authentication) {
        //사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());
        MemberDetailsDto memberDetails = memberService.findMemberDetails(userDetails.getUsername());

        OrderSaveRequestDto dto;

        if (fromCart) {
            //장바구니 주문인 경우, prepareOrderFromCart() 호출
            dto = cartService.prepareOrderFromCart(member.getMemberId());
        } else {
            //단일 상품 주문인 경우
            ItemResponseDto item = itemService.findItem(itemId);
            model.addAttribute("item", item);
            dto = orderService.buildSingleOrderDto(memberDetails, itemId, quantity);
        }

        List<MemberCouponResponseDto> coupons = memberCouponService.getAllCouponsWithUsability(member.getMemberId(), dto.getOrderTotal());

        model.addAttribute("orderSaveRequestDto", dto);
        model.addAttribute("memberDetails", memberDetails);
        model.addAttribute("coupons", coupons);

        return "/pages/order/orderForm";
    }

    @PostMapping("/new")
    public String createOrder(@Valid @ModelAttribute("orderSaveRequestDto") OrderSaveRequestDto orderSaveRequestDto,
                              BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            log.error("주문 데이터 검증 실패: {}", bindingResult.getAllErrors());
            return "/pages/order/orderForm";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        //장바구니에서의 주문인지 확인
        if (orderSaveRequestDto.isFromCart()) {
            orderSaveRequestDto = cartService.prepareOrderFromCart(member.getMemberId());
        } else {
            //단일 상품 주문의 경우 'memberId'가 없으면 추가
            if (orderSaveRequestDto.getMemberId() == null) {
                orderSaveRequestDto.setMemberId(member.getMemberId());
            }
        }

        Long memberCouponId = orderSaveRequestDto.getMemberCouponId();
        int discountAmount = 0;

        if (memberCouponId != null) {
            MemberCoupon memberCoupon = memberCouponService.getOwnedCouponOrThrow(memberCouponId, member.getMemberId());

            int orderTotal = orderSaveRequestDto.getOrderItemDto().stream()
                    .mapToInt(OrderItemDto::getTotalPrice)
                    .sum();

            if (!memberCoupon.isUsable(orderTotal)) {
                throw new IllegalStateException("쿠폰을 사용할 수 없습니다.");
            }

            discountAmount = memberCoupon.getDiscountAmount(orderTotal);
            memberCouponService.useCoupon(memberCouponId, member.getMemberId());
        }

        Long orderId = orderService.placeOrder(orderSaveRequestDto, discountAmount);

        //주문한 상품만 장바구니에서 제거
        if (orderSaveRequestDto.isFromCart()) {
            List<Long> orderedItemIds = orderSaveRequestDto.getOrderItems().stream()
                    .map(OrderItemDto::getItemOptionId)
                    .toList();

            cartService.removeOrderedItemsFromCart(member.getMemberId(), orderedItemIds);
        }

        return "redirect:/order/success?orderId=" + orderId;
    }

    @GetMapping("/history")
    public String findOrders(
            @ModelAttribute("orderPageRequestDto") OrderPageRequestDto requestDto, Pageable pageable,
            Model model, Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());

        Page<OrderResponseDto> orders = orderService.searchOrders(requestDto, pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("orderPageRequestDto", requestDto);
        return "/pages/order/orderHistory";
    }

    @GetMapping("/detail/{orderId}")
    public String orderDetails(@PathVariable("orderId") Long orderId, Model model) {
        OrderResponseDto order = orderService.findOrderById(orderId);
        model.addAttribute("order", order);
        return "/pages/order/orderDetails";
    }

    @GetMapping("/success")
    public String orderSuccess(@RequestParam(value = "orderId", required = false) Long orderId, Model model) {
        if (orderId != null) {
            OrderResponseDto order = orderService.findOrderById(orderId);
            model.addAttribute("order", order);
        }
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

        orderService.applyRefundOrExchange(dto);
        return "redirect:/order/detail/" + dto.getOrderId();
    }
}
