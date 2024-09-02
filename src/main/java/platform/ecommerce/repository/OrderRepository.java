package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMember(Member member);
}
