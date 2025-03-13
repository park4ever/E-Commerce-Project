package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import platform.ecommerce.entity.Member;
import platform.ecommerce.entity.Order;
import platform.ecommerce.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    List<Order> findByMember(Member member);

    long countByCreatedDateAfter(LocalDateTime dateTime);

    @Query("SELECT COALESCE(SUM(oi.orderPrice * oi.count), 0) FROM OrderItem oi")
    long calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(oi.orderPrice * oi.count), 0) FROM OrderItem oi WHERE oi.order.orderDate > :date")
    long calculateRevenueSince(@Param("date") LocalDateTime date);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.order WHERE oi.order.id = :orderId")
    List<OrderItem> findOrderItemsWithOrder(@Param("orderId") Long orderId);
}