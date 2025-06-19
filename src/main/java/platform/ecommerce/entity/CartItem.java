package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import platform.ecommerce.exception.cart.InvalidCartQuantityException;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_item")
public class CartItem extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_option_id", nullable = false)
    private ItemOption itemOption;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int priceSnapshot;

    public static CartItem create(Cart cart, ItemOption option, int quantity) {
        int finalPrice = option.getItem().getFinalPrice();
        return CartItem.builder()
                .cart(cart)
                .itemOption(option)
                .quantity(quantity)
                .priceSnapshot(finalPrice)
                .build();
    }

    //상품 가격 * 수량 계산
    public int getTotalPrice() {
        return this.priceSnapshot * this.quantity;
    }

    //장바구니에서 수량 증가
    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new InvalidCartQuantityException();
        }
        this.quantity += amount;
    }

    //장바구니에서 수량 설정
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new InvalidCartQuantityException();
        }
        this.quantity = newQuantity;
    }

    //Cart 연관관계 설정
    public void associateCart(Cart cart) {
        this.cart = cart;
        if (!cart.getCartItems().contains(this)) {
            cart.getCartItems().add(this);
        }
    }
}
