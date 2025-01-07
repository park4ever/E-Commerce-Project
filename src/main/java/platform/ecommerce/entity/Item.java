package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
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
    private List<Review> reviews = new ArrayList<>();

    //TODO DELETE
//    @Enumerated(STRING)
//    private Category category;

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
//        this.category = category; //TODO DELETE
    }

    /*== 연관관계 편의 메서드 ==*/
    public void addReview(Review review) {
        reviews.add(review);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
    }
}
