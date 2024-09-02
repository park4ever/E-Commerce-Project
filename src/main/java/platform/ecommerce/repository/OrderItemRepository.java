package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
