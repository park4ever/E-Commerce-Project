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
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.dto.member.MemberDetailsDto;
import platform.ecommerce.dto.member.MemberResponseDto;
import platform.ecommerce.dto.order.*;
import platform.ecommerce.entity.OrderStatus;
import platform.ecommerce.service.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping("/new")
    public String orderForm(@RequestParam(value = "itemId", required = false) Long itemId,
                            @RequestParam(value = "quantity", required = false) Integer quantity,
                            @RequestParam(value = "fromCart", required = false, defaultValue = "false") boolean fromCart,
                            Model model, Authentication authentication) {

        log.info("itemId : {}, quantity : {}, fromCart : {}", itemId, quantity, fromCart);

        //사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberResponseDto member = memberService.findMember(userDetails.getUsername());
        MemberDetailsDto memberDetails = memberService.findMemberDetails(userDetails.getUsername());

        OrderSaveRequestDto orderSaveRequestDto;

        if (fromCart) {
            //장바구니 주문인 경우, prepareOrderFromCart() 호출
            log.info("장바구니 기반 주문");
            orderSaveRequestDto = cartService.prepareOrderFromCart(member.getMemberId());
        } else {
            //단일 상품 주문인 경우
            log.info("단일 상품 주문");
            ItemResponseDto item = itemService.findItem(itemId);
            log.info("상품 정보 - itemId : {}, iteName : {}, imageUrl : {}", item.getId(), item.getItemName(), item.getImageUrl());
            model.addAttribute("item", item);

            orderSaveRequestDto = orderService.createOrderSaveRequestDto(memberDetails, itemId, quantity);
        }

        model.addAttribute("orderSaveRequestDto", orderSaveRequestDto);
        model.addAttribute("memberDetails", memberDetails);

        log.info("Order 요청 - memberId : {}, orderDate : {}", orderSaveRequestDto.getMemberId(), orderSaveRequestDto.getOrderDate());

        return "/pages/order/orderForm";
    }

    @PostMapping("/new")
    public String createOrder(@Valid @ModelAttribute("orderSaveRequestDto") OrderSaveRequestDto orderSaveRequestDto,
                              BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            log.error("OrderSaveRequestDto binding errors : {}", bindingResult.getAllErrors());
            return "/pages/order/orderForm";
        }

        //장바구니에서 주문하는 경우, orderSaveRequestDto를 생성하도록 변경
        if (orderSaveRequestDto.isFromCart()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            MemberResponseDto member = memberService.findMember(userDetails.getUsername());

            orderSaveRequestDto = cartService.prepareOrderFromCart(member.getMemberId());
        }

        Long orderId = orderService.createOrder(orderSaveRequestDto);
        log.info("주문 완료 - 주문 ID : {}, 회원 ID : {}", orderId, orderSaveRequestDto.getMemberId());

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
