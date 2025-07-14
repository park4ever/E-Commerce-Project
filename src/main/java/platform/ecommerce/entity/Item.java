package platform.ecommerce.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

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

    /**
     * 기본 식별자
     */
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    /**
     * 상품 기본 정보
     */
    @Column(nullable = false, length = 30)
    private String itemName;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    @Column(length = 20)
    private String brand;

    /**
     * 가격 정보
     */
    @Column(nullable = false)
    private int price;

    @Column(name = "discount_price")
    private Integer discountPrice;

    /**
     * 판매 상태 정보
     */
    @Column(nullable = false)
    private boolean isAvailable = true; //시스템 기준 판매 가능 여부

    @Column(nullable = false)
    private boolean isSelling = true; //관리자에 의한 판매 가능 상태

    /**
     * 통계 정보
     */
    @Column(nullable = false)
    private long viewCount = 0L; //상세 페이지 진입 시 자동 증가

    /**
     * 연관 관계
     */
    @OneToMany(mappedBy = "item", cascade = REMOVE)
    @BatchSize(size = 100)
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = ALL, orphanRemoval = true)
    private List<ItemOption> itemOptions = new ArrayList<>();

    /** ===============================
     * 비즈니스 로직
     ================================ */

    //가격 관련
    public int getFinalPrice() {
        return discountPrice != null ? discountPrice : price;
    }

    public boolean isDiscounted() {
        return discountPrice != null && discountPrice < price;
    }

    public void applyDiscountPrice(@Nullable Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    //상태 제어
    public void makeAvailable() {
        this.isAvailable = true;
    }

    public void makeUnavailable() {
        this.isAvailable = false;
    }

    public void startSelling() {
        this.isSelling = true;
    }

    public void stopSelling() {
        this.isSelling = false;
    }

    //조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    //상품 정보 수정
    public void updateItemDetails(String itemName, String description, int price, ItemCategory category, String brand, boolean isSelling) {
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.brand = brand;
        this.isSelling = isSelling;
    }

    //이미지 변경
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    //연관관계 편의 메서드
    public void addItemOption(ItemOption option) {
        itemOptions.add(option);
        option.associateItem(this);
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
    }
}