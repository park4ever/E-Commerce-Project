package platform.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.ecommerce.entity.Cart;
import platform.ecommerce.entity.CartItem;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    List<CartItem> findByCart(Cart cart);
}
