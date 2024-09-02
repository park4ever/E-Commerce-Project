package platform.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @JsonIgnore //양방향 연관 관계 무한 루프 방지
    @OneToMany(mappedBy = "member", cascade = ALL)
    private List<Order> orders = new ArrayList<>();
}
