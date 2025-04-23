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

    public static Cart create(Member member) {
        return Cart.builder()
                .member(member)
                .cartItems(new ArrayList<>())
                .build();
    }

    public void clearItems() {
        this.cartItems.clear();
    }

    public void removeItemsByOptionIds(List<Long> optionIds) {
        cartItems.removeIf(cartItem ->
                optionIds.contains(cartItem.getItemOption().getId())
        );
    }

    public void addItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.associateCart(this); //직접 연관관계 설정 메서드 호출
    }

    public void removeItem(CartItem cartItem) {
        this.cartItems.remove(cartItem);
    }

    public int getTotalPrice() {
        return cartItems.stream()
                .mapToInt(CartItem::getTotalPrice)
                .sum();
    }
}
