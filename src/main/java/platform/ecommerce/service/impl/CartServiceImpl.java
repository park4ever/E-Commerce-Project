package platform.ecommerce.service.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.cart.CartItemDto;
import platform.ecommerce.dto.order.OrderItemDto;
import platform.ecommerce.dto.order.OrderSaveRequestDto;
import platform.ecommerce.entity.*;
import platform.ecommerce.repository.CartItemRepository;
import platform.ecommerce.repository.CartRepository;
import platform.ecommerce.repository.ItemRepository;
import platform.ecommerce.repository.MemberRepository;
import platform.ecommerce.service.CartService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static platform.ecommerce.entity.PaymentMethod.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final EntityManager em;

    @Override
    public void addItemToCart(Long memberId, Long itemId, int quantity) {
        log.info("Adding item to cart : memberId = {}, itemId = {}, quantity = {}", memberId, itemId, quantity);
        //회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        log.info("Member found : {}", member);

        //장바구니 조회, 없으면 생성
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .member(member)
                            .cartItems(new ArrayList<>()) //빈 리스트로 초기화
                            .build();
                    return cartRepository.save(newCart);
                });
        log.info("Cart found or created : {}", cart);

        //장바구니에 상품 조회, 없으면 추가
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), itemId);
        if (cartItem == null) {
            //장바구니에 해당 상품이 없으면 추가
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
            log.info("Item found : {}", item);

            validateItemQuantity(quantity);

            cartItem = CartItem.builder()
                    .cart(cart)
                    .item(item)
                    .quantity(quantity)
                    .build();
            cartItemRepository.save(cartItem);
            cart.getCartItems().add(cartItem); //새 아이템을 장바구니에 추가
        } else {
            //장바구니에 이미 있는 상품이라면 수량 증가
            cartItem.increaseQuantity(quantity);
        }

        log.info("Cart updated : {}", cart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItems(Long memberId) {
        log.info("Fetching cart for member ID : {}", memberId);
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElse(null);

        if (cart == null) {
            log.warn("Cart not found for member ID : {}", memberId);
            return new ArrayList<>(); //장바구니가 없을 경우 빈 리스트 반환
        }

        log.info("Found cart : {}", cart);

        return cart.getCartItems().stream()
                .map(cartItem -> new CartItemDto(
                        cartItem.getItem().getId(),
                        cartItem.getItem().getItemName(),
                        cartItem.getItem().getPrice(),
                        cartItem.getQuantity(),
                        cartItem.getItem().getImageUrl()
                ))
                .collect(toList());
    }

    @Override
    public void updateItemQuantity(Long memberId, Long itemId, int quantity) {
        CartItem cartItem = getCartInfo(memberId, itemId);

        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }

        //기존 수량을 기반으로 새로운 수량으로 업데이트
        cartItem.updateQuantity(quantity);
    }

    @Override
    public void removeItemFromCart(Long memberId, Long itemId) {
        CartItem cartItem = getCartInfo(memberId, itemId);
        if (cartItem != null) {
            cartItemRepository.delete(cartItem);
        } else {
            throw new IllegalArgumentException("장바구니에 상품이 없습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateCartTotal(List<CartItemDto> cartItems) {
        return cartItems.stream()
                .mapToInt(cartItem -> cartItem.getItemPrice() * cartItem.getQuantity())
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> findCartItems(Long memberId) {
        Cart cart = getCartByMemberId(memberId);

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        return cartItems.stream()
                .map(cartItem -> new CartItemDto(
                        cartItem.getItem().getId(),
                        cartItem.getItem().getItemName(),
                        cartItem.getItem().getPrice(),
                        cartItem.getQuantity(),
                        cartItem.getItem().getImageUrl()))
                .collect(toList());
    }

    @Override
    public OrderSaveRequestDto prepareOrderFromCart(Long memberId) {
        Cart cart = getCartByMemberId(memberId);

        if (cart.getCartItems().isEmpty()) {
            log.warn("Cart is empty for member ID : {}", memberId);
            return createEmptyOrder(memberId);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

        return createOrderFromCart(cart, member);
    }

    @Override
    public void clearCartAfterOrder(Long memberId) {
        log.info("Clearing cart for member ID : {}", memberId);
        clearCart(memberId);
    }

    @Override
    public void clearCart(Long memberId) {
        log.info("Clearing cart for member ID : {}", memberId);
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElse(null);

        if (cart == null) {
            log.warn("Cart not found for member ID : {}", memberId);
            return;
        }

        cart.getCartItems().clear();
        cartRepository.save(cart);

        em.flush();
        em.clear();

        log.info("Cart cleared successfully!");
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(Long memberId) {
        /*Cart cart = cartRepository.findByMemberId(memberId).orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        return (cart != null) ? cart.getCartItems().size() : 0;*/
        return cartRepository.findByMemberId(memberId)
                .map(cart -> cart.getCartItems().size())
                .orElse(0); //예외 대신 기본값 0을 반환
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
                .map(this::convertToOrderItemDto)
                .collect(toList());

        return OrderSaveRequestDto.builder()
                .memberId(member.getId())
                .orderDate(LocalDateTime.now())
                .orderItems(orderItems)
                .customerName(member.getUsername())
                .customerPhone(member.getPhoneNumber())
                .customerAddress(member.getAddress())
                .shippingAddress(member.getAddress())   //기본값으로 설정(UI에서 선택 시 덮어씌어짐)
                .paymentMethod(CARD)   //기본값으로 설정(UI에서 선택 시 덮어씌어짐)
                .fromCart(true)
                .build();
    }

    private OrderItemDto convertToOrderItemDto(CartItem cartItem) {
        return new OrderItemDto(
                cartItem.getItem().getId(),
                cartItem.getItem().getItemName(),
                cartItem.getItem().getPrice(),
                cartItem.getQuantity(),
                cartItem.getItem().getImageUrl()
        );
    }

    private Cart getCartByMemberId(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("장바구니를 찾을 수 없습니다. 먼저 상품을 추가하세요."));
    }

    private void validateItemQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
    }

    private void restoreStock(Cart cart) {
        for (CartItem cartItem : cart.getCartItems()) {
            Item item = cartItem.getItem();
            item.addStock(cartItem.getQuantity());
        }
    }

    private CartItem getCartInfo(Long memberId, Long itemId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니를 찾을 수 없습니다."));
        if (cart == null) {
            throw new IllegalArgumentException("장바구니가 존재하지 않습니다.");
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), itemId);
        if (cartItem == null) {
            throw new IllegalArgumentException("장바구니에 해당 상품이 없습니다.");
        }
        return cartItem;
    }
}
