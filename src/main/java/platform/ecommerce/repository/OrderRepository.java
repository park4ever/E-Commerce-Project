package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.Order;
import platform.ecommerce.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    List<Order> findByMember(Member member);
}
