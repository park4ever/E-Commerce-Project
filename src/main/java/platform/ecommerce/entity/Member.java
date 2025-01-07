package platform.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table(name = "member")
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @JsonIgnore //양방향 연관 관계 무한 루프 방지
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @Column(nullable = false)
    private String phoneNumber;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @Column
    private LocalDate dateOfBirth;

    @Column
    private String defaultShippingAddress; //TODO 제거할 생각중..

    @OneToMany(mappedBy = "member", cascade = REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public void updateMemberInfo(String username, String phoneNumber, Address address, LocalDate dateOfBirth) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /*== 연관관계 편의 메서드 ==*/
    public void addReview(Review review) {
        reviews.add(review);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
    }
}
