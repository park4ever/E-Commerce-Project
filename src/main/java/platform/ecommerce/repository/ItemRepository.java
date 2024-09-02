package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
