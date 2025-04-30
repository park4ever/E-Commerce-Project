package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.cart.CartItemDto;
import platform.ecommerce.dto.order.OrderItemDto;
import platform.ecommerce.dto.order.OrderSaveRequestDto;
import platform.ecommerce.entity.*;
import platform.ecommerce.repository.*;
import platform.ecommerce.service.CartService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.*;
import static platform.ecommerce.entity.PaymentMethod.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final MemberRepository memberRepository;

    @Override
    public void addToCart(Long memberId, Long itemOptionId, int quantity) {
        //회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        //장바구니 조회 or 생성
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> cartRepository.save(Cart.create(member)));

        //상품 옵션 조회
        ItemOption option = itemOptionRepository.findById(itemOptionId)
                .orElseThrow(() -> new IllegalArgumentException("상품 옵션을 찾을 수 없습니다."));

        //해당 옵션으로 이미 담긴 CartItem이 있는지 확인
        CartItem cartItem = cartItemRepository.findByCartIdAndItemOptionId(cart.getId(), itemOptionId);

        if (cartItem == null) {
            //새로 담는 경우 -> 재고 검증 후 추가
            validateQuantity(option, quantity);

            cartItem = CartItem.create(cart, option, quantity);
            cartItemRepository.save(cartItem);
            cart.addItem(cartItem);
        } else {
            //기존에 있던 상품 -> 총 수량 기준으로 검증
            int newTotalQuantity = cartItem.getQuantity() + quantity;
            validateQuantity(option, newTotalQuantity);

            cartItem.increaseQuantity(quantity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItems(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId).orElse(null);

        if (cart == null) {
            return new ArrayList<>(); //장바구니가 없을 경우 빈 리스트 반환
        }

        return cart.getCartItems().stream()
                .filter(cartItem -> isItemDisplayable(cartItem.getItemOption()))
                .map(cartItem -> {
                    ItemOption option = cartItem.getItemOption();
                    Item item = option.getItem();

                    return new CartItemDto(
                            option.getId(),
                            item.getItemName(),
                            option.getSizeLabel(),
                            cartItem.getPriceSnapshot(),
                            cartItem.getQuantity(),
                            item.getImageUrl()
                    );
                })
                .collect(toList());
    }

    @Override
    public void updateQuantity(Long memberId, Long cartItemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }

        CartItem cartItem = getCartItem(memberId, cartItemId);
        cartItem.updateQuantity(quantity);
    }

    @Override
    public void removeFromCart(Long memberId, Long cartItemId) {
        CartItem cartItem = getCartItem(memberId, cartItemId);
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateTotalPrice(List<CartItemDto> cartItems) {
        return cartItems.stream()
                .mapToInt(CartItemDto::getTotalPrice)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> findCartItems(Long memberId) {
        Cart cart = getCartByMemberId(memberId);

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        return cartItems.stream()
                .map(cartItem -> {
                    ItemOption option = cartItem.getItemOption();
                    Item item = option.getItem();

                    return new CartItemDto(
                            option.getId(),
                            item.getItemName(),
                            option.getSizeLabel(),
                            cartItem.getPriceSnapshot(),
                            cartItem.getQuantity(),
                            item.getImageUrl()
                    );
                })
                .collect(toList());
    }

    @Override
    public OrderSaveRequestDto prepareOrderFromCart(Long memberId) {
        Cart cart = getCartByMemberId(memberId);

        if (cart.getCartItems().isEmpty()) {
            return createEmptyOrder(memberId);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        return createOrderFromCart(cart, member);
    }

    @Override
    public void clearCart(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElse(null);

        if (cart != null) {
            cart.clearItems();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .map(cart -> cart.getCartItems().size())
                .orElse(0); //예외 대신 기본값 0을 반환
    }

    @Override
    public void removeOrderedItemsFromCart(Long memberId, List<Long> orderedItemOptionIds) {
        Cart cart = cartRepository.findByMemberId(memberId).orElse(null);

        if (cart == null) return;

        cart.removeItemsByOptionIds(orderedItemOptionIds);
    }

    private boolean isItemDisplayable(ItemOption option) {
        return option != null && option.isAvailable() && option.getStockQuantity() > 0;
    }

    private OrderSaveRequestDto createEmptyOrder(Long memberId) {
        return OrderSaveRequestDto.builder()
                .memberId(memberId)
                .orderDate(LocalDateTime.now())
                .orderItems(new ArrayList<>()) //장바구니가 비었을 때
                .fromCart(true)
                .build();
    }

    private OrderSaveRequestDto createOrderFromCart(Cart cart, Member member) {
        List<OrderItemDto> orderItems = cart.getCartItems().stream()
                .map(cartItem -> OrderItemDto.from(
                        cartItem.getItemOption(),
                        cartItem.getQuantity()
                ))
                .collect(toList());

        return OrderSaveRequestDto.builder()
                .memberId(member.getId())
                .orderDate(LocalDateTime.now())
                .orderItems(orderItems)
                .customerName(member.getUsername())
                .customerPhone(member.getPhoneNumber())
                .customerAddress(member.getAddress())
                .shippingAddress(member.getAddress())   //기본값으로 설정(UI에서 선택 시 덮어씌어짐)
                .paymentMethod(CARD)                    //TODO
                .fromCart(true)
                .build();
    }

    private Cart getCartByMemberId(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("장바구니를 찾을 수 없습니다. 먼저 상품을 추가하세요."));
    }

    private void validateQuantity(ItemOption option, int requestedQuantity) {
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        if (option.getStockQuantity() < requestedQuantity) {
            throw new IllegalArgumentException("해당 옵션의 재고가 부족합니다.");
        }
    }

    private CartItem getCartItem(Long memberId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

        if (!cartItem.getCart().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 장바구니 항목에 대한 권한이 없습니다.");
        }

        return cartItem;
    }
}