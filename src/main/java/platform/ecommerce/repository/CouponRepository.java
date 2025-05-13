package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom {
}
