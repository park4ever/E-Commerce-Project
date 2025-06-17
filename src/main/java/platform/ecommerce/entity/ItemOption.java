package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import platform.ecommerce.exception.item.NotEnoughStockException;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "item_option")
public class ItemOption extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "item_option_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private String sizeLabel;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private boolean isAvailable;

    public static ItemOption create(Item item, String sizeLabel, int stockQuantity) {
        ItemOption option = ItemOption.builder()
                .item(item)
                .sizeLabel(sizeLabel)
                .stockQuantity(stockQuantity)
                .isAvailable(stockQuantity > 0)
                .build();
        option.associateItem(item);

        return option;
    }

    public void updateStockQuantity(int quantity) {
        if (this.stockQuantity != quantity) {
            this.stockQuantity = quantity;
        }
    }

    public void removeStock(int quantity) {
        int remain = this.stockQuantity - quantity;
        if (remain < 0) {
            throw new NotEnoughStockException("상품의 재고가 부족합니다.");
        }
        this.stockQuantity = remain;
        this.isAvailable = remain > 0;
    }

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
        this.isAvailable = true;
    }

    public boolean isSoldOut() {
        return this.stockQuantity <= 0;
    }

    public void deactivate() {
        this.isAvailable = false;
    }

    public void associateItem(Item item) {
        this.item = item;
        if (!item.getItemOptions().contains(this)) {
            item.getItemOptions().add(this);
        }
    }
}
