package platform.ecommerce.dto.admin;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminReviewDto {

    private Long id;
    private String memberEmail;
    private Long itemId;
    private String itemName;
    private String content;
    private int rating;
    private String imageUrl;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
