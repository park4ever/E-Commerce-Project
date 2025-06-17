package platform.ecommerce.entity;

import jakarta.annotation.Nullable;
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

    @Column(nullable = false, length = 30)
    private String itemName;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    @OneToMany(mappedBy = "item", cascade = REMOVE)
    @BatchSize(size = 100)
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = ALL, orphanRemoval = true)
    private List<ItemOption> itemOptions = new ArrayList<>();

    @Column(nullable = false)
    private boolean isAvailable = true; //상품 활성화 여부

    public int getFinalPrice() {
        return discountPrice != null ? discountPrice : price;
    }

    public boolean isDiscounted() {
        return discountPrice != null && discountPrice < price;
    }

    public void addItemOption(ItemOption option) {
        itemOptions.add(option);
        option.associateItem(this);
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateItemDetails(String itemName, String description, int price, ItemCategory category) {
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public void applyDiscountPrice(@Nullable Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    public void deactivate() {
        this.isAvailable = false;
    }

    public void activate() {
        this.isAvailable = true;
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
    }
}
