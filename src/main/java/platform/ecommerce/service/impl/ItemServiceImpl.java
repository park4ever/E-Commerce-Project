package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.ecommerce.dto.ItemResponseDto;
import platform.ecommerce.dto.ItemSaveRequestDto;
import platform.ecommerce.dto.ItemUpdateDto;
import platform.ecommerce.entity.Item;
import platform.ecommerce.repository.ItemRepository;
import platform.ecommerce.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Long saveItem(ItemSaveRequestDto itemSaveRequestDto) {
        Item item = Item.builder()
                .itemName(itemSaveRequestDto.getItemName())
                .description(itemSaveRequestDto.getDescription())
                .price(itemSaveRequestDto.getPrice())
                .stockQuantity(itemSaveRequestDto.getStockQuantity())
                .build();

        return itemRepository.save(item).getId();
    }

    @Override
    public List<ItemResponseDto> findItems() {
        List<Item> items = itemRepository.findAll();

        return items.stream()
                .map(item -> ItemResponseDto.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .description(item.getDescription())
                        .price(item.getPrice())
                        .stockQuantity(item.getStockQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto findItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return ItemResponseDto.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .build();
    }

    @Override
    @Transactional
    public void updateItem(Long id, ItemUpdateDto itemUpdateDto) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        Item updatedItem = Item.builder()
                .id(existingItem.getId()) //기존 엔티티의 ID를 사용
                .itemName(itemUpdateDto.getItemName())
                .description(itemUpdateDto.getDescription())
                .price(itemUpdateDto.getPrice())
                .stockQuantity(itemUpdateDto.getStockQuantity())
                .build();
        itemRepository.save(updatedItem);
    }
}
