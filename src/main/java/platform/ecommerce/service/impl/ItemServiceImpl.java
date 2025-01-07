package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.dto.item.ItemSaveRequestDto;
import platform.ecommerce.dto.item.ItemSearchCondition;
import platform.ecommerce.dto.item.ItemUpdateDto;
import platform.ecommerce.entity.Category;
import platform.ecommerce.entity.Item;
import platform.ecommerce.repository.ItemRepository;
import platform.ecommerce.service.ItemService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public Long saveItem(ItemSaveRequestDto itemSaveRequestDto) {
        //이미지 파일 저장
        String imageUrl = saveImage(itemSaveRequestDto.getImage());

        Item item = Item.builder()
                .itemName(itemSaveRequestDto.getItemName())
                .description(itemSaveRequestDto.getDescription())
                .price(itemSaveRequestDto.getPrice())
                .stockQuantity(itemSaveRequestDto.getStockQuantity())
                .imageUrl(imageUrl)
//                .category(itemSaveRequestDto.getCategory()) //TODO DELETE
                .build();

        return itemRepository.save(item).getId();
    }

    @Override
    public List<ItemResponseDto> findItems() {
        List<Item> items = itemRepository.findAll();

        return items.stream().map(item -> ItemResponseDto.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .description(item.getDescription())
                        .price(item.getPrice())
                        .stockQuantity(item.getStockQuantity())
                        .imageUrl("/images/" + item.getImageUrl())
//                        .category(item.getCategory()) //TODO DELETE
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Page<ItemResponseDto> findItemsWithPageable(ItemSearchCondition cond, Pageable pageable) {
        Page<Item> itemsPage = itemRepository.findItemsByCondition(cond, pageable);

        return itemsPage.map(item -> ItemResponseDto.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .imageUrl("/images/" + item.getImageUrl())
//                .category(item.getCategory()) //TODO DELETE
                .build());
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
                .imageUrl("/images/" + item.getImageUrl())
//                .category(item.getCategory()) //TODO DELETE
                .build();
    }

    @Override
    @Transactional
    public void updateItem(Long id, ItemUpdateDto itemUpdateDto) {
        //상품을 찾아서 없으면 예외 발생
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        //이미지 파일을 새로 업로드하는 경우 처리
        if (itemUpdateDto.getImage() != null && !itemUpdateDto.getImage().isEmpty()) {
            String imageUrl = saveImage(itemUpdateDto.getImage());
            existingItem.updateImageUrl(imageUrl);
        }

        //수정된 필드가 있을 경우 처리
        existingItem.updateItemDetails(
                itemUpdateDto.getItemName() != null ? itemUpdateDto.getItemName() : existingItem.getItemName(),
                itemUpdateDto.getDescription() != null ? itemUpdateDto.getDescription() : existingItem.getDescription(),
                itemUpdateDto.getPrice() != null ? itemUpdateDto.getPrice() : existingItem.getPrice(),
                itemUpdateDto.getStockQuantity() != null ? itemUpdateDto.getStockQuantity() : existingItem.getStockQuantity()
//                itemUpdateDto.getCategory() != null ? itemUpdateDto.getCategory() : existingItem.getCategory() //TODO DELETE
        );
    }

    @Override
    public ItemUpdateDto convertToUpdateDto(ItemResponseDto itemResponseDto) {
        return ItemUpdateDto.builder()
                .itemName(itemResponseDto.getItemName())
                .description(itemResponseDto.getDescription())
                .price(itemResponseDto.getPrice())
                .stockQuantity(itemResponseDto.getStockQuantity())
                .imageUrl(itemResponseDto.getImageUrl())
                .build();
    }

    private String saveImage(MultipartFile imageFile) {
        //상품 이미지 파일이 없으면 기본 이미지로 처리
        if (imageFile == null || imageFile.isEmpty()) {
            //기본 이미지 파일명을 UUID와 결합하여 생성
            String defaultImageName = UUID.randomUUID() + "_" + "default.png";
            Path defaultImagePath = Paths.get(uploadDir, defaultImageName);

            //기본 이미지를 지정된 경로에 복사
            try (InputStream inputStream = getClass().getResourceAsStream("/static/item/default.png")) {
                Files.copy(inputStream, defaultImagePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("기본 이미지 생성에 실패하였습니다. ", e);
            }

            return defaultImageName;
        }

        //상품 이미지 파일이 입력되어있을 경우, 원래의 저장 로직 수행
        try {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName; //파일명 저장
        } catch (IOException e) {
            throw new RuntimeException("상품 이미지 저장에 실패하였습니다. ", e);
        }
    }
}
