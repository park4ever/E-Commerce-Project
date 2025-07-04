package platform.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private int rating; // 1~5 사이의 별점

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private boolean isVisible = true;

    @Column(columnDefinition = "TEXT")
    private String adminReply;

    public static Review createReview(Item item, Member member, String content, int rating) {
        Review review = new Review();
        review.item = item;
        review.member = member;
        review.content = content;
        review.rating = rating;

        review.assignItem(item);      //Item 엔티티에 Review 추가
        review.linkMember(member);    //Member 엔티티에 Review 추가
        return review;
    }

    public void updateReview(String content, int rating) {
        this.content = content;
        this.rating = rating;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void assignItem(Item item) {
        this.item = item;
        if (!item.getReviews().contains(this)) {
            item.getReviews().add(this);
        }
    }

    public void linkMember(Member member) {
        if (!member.getReviews().contains(this)) {
            member.getReviews().add(this);
        }
    }

    public void toggleVisibility(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void addAdminReply(String reply) {
        this.adminReply = reply;
    }

    public void removeAdminReply() {
        this.adminReply = null;
    }
}
