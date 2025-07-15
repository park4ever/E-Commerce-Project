package platform.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.ecommerce.dto.item.*;

import java.util.List;

public interface ItemService {

    Long saveItem(ItemSaveRequestDto saveRequestDto);

    List<ItemResponseDto> findItems();

    Page<ItemResponseDto> findItemsWithPageable(ItemPageRequestDto requestDto, Pageable pageable);

    ItemResponseDto findItem(Long id);

    ItemResponseDto findItemWithViewCount(Long id);

    void updateItem(Long id, ItemUpdateDto updateDto);

    ItemUpdateDto convertToUpdateDto(ItemResponseDto responseDto);
}
