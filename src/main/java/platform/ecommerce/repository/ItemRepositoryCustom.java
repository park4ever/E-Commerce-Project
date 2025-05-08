package platform.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.item.ItemPageRequestDto;
import platform.ecommerce.entity.Item;

public interface ItemRepositoryCustom {
    Page<Item> searchItems(ItemPageRequestDto requestDto, Pageable pageable);

    int getTotalSalesByItemId(Long itemId);
}
