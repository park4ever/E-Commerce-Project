package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import platform.ecommerce.dto.item.ItemPageRequestDto;
import platform.ecommerce.dto.item.ItemSearchCondition;
import platform.ecommerce.entity.Item;

import java.util.List;

public interface ItemRepositoryCustom {
    Page<Item> findItemsByCondition(ItemSearchCondition cond, Pageable pageable);

    Page<Item> searchItems(ItemPageRequestDto requestDto, Pageable pageable);

    int getTotalSalesByItemId(Long itemId);
}
