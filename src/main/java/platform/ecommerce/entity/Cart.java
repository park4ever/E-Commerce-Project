package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "cart_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    //장바구니에 상품 추가
    public void addItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.associateCart(this); //직접 연관관계 설정 메서드 호출
    }

    //장바구니에서 상품 제거
    public void removeItem(CartItem cartItem) {
        this.cartItems.remove(cartItem);
        cartItem.disassociateCart(); //직접 연관관계 해제 메서드 호출
    }

    //장바구니 모든 품목 가격 계산
    public int getTotalPrice() {
        return cartItems.stream()
                .mapToInt(CartItem::getTotalPrice)
                .sum();
    }
}
