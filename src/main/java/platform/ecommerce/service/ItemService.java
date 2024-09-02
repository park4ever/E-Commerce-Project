package platform.ecommerce.service;

import platform.ecommerce.dto.ItemResponseDto;
import platform.ecommerce.dto.ItemSaveRequestDto;
import platform.ecommerce.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    Long saveItem(ItemSaveRequestDto itemSaveRequestDto);

    List<ItemResponseDto> findItems();

    ItemResponseDto findItem(Long id);

    void updateItem(Long id, ItemUpdateDto itemUpdateDto);
}
