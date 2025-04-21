package platform.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.dto.item.ItemSaveRequestDto;
import platform.ecommerce.dto.item.ItemSearchCondition;
import platform.ecommerce.dto.item.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    Long saveItem(ItemSaveRequestDto saveRequestDto);

    List<ItemResponseDto> findItems();

    Page<ItemResponseDto> findItemsWithPageable(ItemSearchCondition cond, Pageable pageable);

    ItemResponseDto findItem(Long id);

    void updateItem(Long id, ItemUpdateDto updateDto);

    ItemUpdateDto convertToUpdateDto(ItemResponseDto responseDto);
}
