package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import platform.ecommerce.Exception.NotEnoughStockException;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item")
public class Item extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    private int stockQuantity;

    /* == 비즈니스 로직 == */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고가 0 보다 작을 수 없습니다.");
        }
        this.stockQuantity = restStock;
    }
}
