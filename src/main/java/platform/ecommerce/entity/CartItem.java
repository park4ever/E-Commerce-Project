package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private int quantity;

    @Builder
    public CartItem(Cart cart, Item item, int quantity) {
        this.cart = cart;
        this.item = item;
        this.quantity = quantity;
    }

    //상품 가격 * 수량 계산
    public int getTotalPrice() {
        return this.item.getPrice() * this.quantity;
    }

    //장바구니에서 수량 증가
    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        this.quantity += amount;
    }

    //장바구니에서 수량 감소
    public void decreaseQuantity(int decreaseBy) {
        if (decreaseBy <= 0) {
            throw new IllegalArgumentException("감소할 수량은 0보다 커야 합니다.");
        }
        if (this.quantity - decreaseBy < 0) {
            throw new IllegalArgumentException("수량이 0보다 작을 수 없습니다.");
        }
        this.quantity -= decreaseBy;
    }

    //장바구니에서 수량 설정
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        this.quantity = newQuantity;
    }

    //Cart와 연관관계 설정을 위한 메서드
    public void associateCart(Cart cart) {
        this.cart = cart;
    }

    //Cart와 연관관계 해제를 위한 메서드
    public void disassociateCart() {
        this.cart = null;
    }
}
