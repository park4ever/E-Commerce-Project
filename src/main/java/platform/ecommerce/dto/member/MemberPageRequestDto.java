package platform.ecommerce.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.springframework.data.domain.Sort.*;
import static org.springframework.data.domain.Sort.Direction.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberPageRequestDto {

    private String searchKeyword = "";      //검색어 입력
    private int page = 0;                   //현재 페이지
    private int size = 10;                  //한 페이지에 보여줄 데이터 개수
    private String sortBy = "createdDate";  //정렬 기준 필드
    private Direction direction = DESC;     //정렬 방향

    public Pageable toPageable() {          //Pageable 객체로 변환
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}
