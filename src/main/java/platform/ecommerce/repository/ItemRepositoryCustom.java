package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.item.ItemSearchCondition;
import platform.ecommerce.entity.Item;

public interface ItemRepositoryCustom {
    Page<Item> findItemsByCondition(ItemSearchCondition cond, Pageable pageable);
}
