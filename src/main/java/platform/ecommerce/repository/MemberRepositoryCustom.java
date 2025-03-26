package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.entity.Member;

public interface MemberRepositoryCustom {

    Page<Member> searchMembers(String keyword, String field, Pageable pageable);
}
