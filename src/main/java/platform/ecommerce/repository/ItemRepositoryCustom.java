package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import platform.ecommerce.dto.item.ItemSearchCondition;
import platform.ecommerce.entity.Item;

import java.util.List;

public interface ItemRepositoryCustom {
    Page<Item> findItemsByCondition(ItemSearchCondition cond, Pageable pageable);

    List<Item> searchItems(String searchKeyword, Sort sort);

    int getTotalSalesByItemId(Long itemId);
}
