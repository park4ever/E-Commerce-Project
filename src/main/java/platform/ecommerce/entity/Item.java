package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import platform.ecommerce.exception.NotEnoughStockException;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;

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

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "item", cascade = REMOVE)
    @BatchSize(size = 100)
    private List<Review> reviews = new ArrayList<>();

    @Column(nullable = false)
    private boolean isAvailable = true; //상품 활성화 여부

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
        this.isAvailable = this.stockQuantity > 0; //재고가 추가되면 활성화
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고가 0 보다 작을 수 없습니다.");
        }
        this.stockQuantity = restStock;
        this.isAvailable = restStock > 0; //재고가 0이면 비활성화
    }

    public boolean isOutOfStock(int count) {
        return this.stockQuantity < count;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateItemDetails(String itemName, String description, int price, int stockQuantity) {
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.isAvailable = stockQuantity > 0;
    }

    public void deactivate() {
        this.isAvailable = false; //상품 비활성화
    }

    public void activate() {
        //재고가 있을 때만 활성화
        if (this.stockQuantity > 0) {
            this.isAvailable = true;
        }
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
    }
}
