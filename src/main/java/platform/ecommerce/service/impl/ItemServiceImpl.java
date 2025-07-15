package platform.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import platform.ecommerce.config.FileUploadProperties;
import platform.ecommerce.dto.item.*;
import platform.ecommerce.entity.Item;
import platform.ecommerce.entity.ItemCategory;
import platform.ecommerce.entity.ItemOption;
import platform.ecommerce.exception.item.ItemNotFoundException;
import platform.ecommerce.exception.item.ItemOptionRequiredException;
import platform.ecommerce.repository.ItemRepository;
import platform.ecommerce.service.ItemService;
import platform.ecommerce.service.upload.FileStorageService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final FileStorageService fileStorageService;
    private final FileUploadProperties properties;

    private static final String DEFAULT_IMAGE_NAME = "default.png";

    @Override
    @Transactional
    public Long saveItem(ItemSaveRequestDto requestDto) {
        if (requestDto.getOptions() == null || requestDto.getOptions().isEmpty()) {
            throw new ItemOptionRequiredException();
        }

        //이미지 저장
        String imageUrl = saveImage(requestDto.getImage());

        //Item 엔티티 생성
        Item item = Item.builder()
                .itemName(requestDto.getItemName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .imageUrl(imageUrl)
                .category(requestDto.getCategory())
                .brand(requestDto.getBrand())
                .isSelling(requestDto.isSelling())
                .isAvailable(true)
                .build();

        //옵션 생성 및 연관관계 설정
        requestDto.getOptions().forEach(optionDto -> {
            ItemOption option = ItemOption.create(
                    item,
                    optionDto.getSizeLabel(),
                    optionDto.getStockQuantity()
            );
            item.addItemOption(option);
        });

        //저장
        return itemRepository.save(item).getId();
    }

    @Override
    public List<ItemResponseDto> findItems() {
        return itemRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ItemResponseDto> findItemsWithPageable(ItemPageRequestDto requestDto, Pageable pageable) {
        return itemRepository.searchItems(requestDto, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ItemResponseDto findItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
        return mapToResponse(item);
    }

    @Override
    public ItemResponseDto findItemWithViewCount(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);

        item.increaseViewCount();

        return mapToResponse(item);
    }

    @Override
    @Transactional
    public void updateItem(Long id, ItemUpdateDto updateDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);

        //이미지 수정
        updateImageIfPresent(item, updateDto.getImage());
        //기본 필드 수정
        updateItemFields(item, updateDto);
        //옵션 전체 교체
        replaceItemOptions(item, updateDto.getOptions());
    }

    @Override
    public ItemUpdateDto convertToUpdateDto(ItemResponseDto responseDto) {
        return ItemUpdateDto.builder()
                .itemName(responseDto.getItemName())
                .description(responseDto.getDescription())
                .price(responseDto.getPrice())
                .discountPrice(responseDto.getDiscountPrice())
                .imageUrl(responseDto.getImageUrl())
                .category(responseDto.getCategory())
                .brand(responseDto.getBrand())
                .isSelling(responseDto.getIsSelling())
                .options(responseDto.getOptions())
                .build();
    }

    private ItemResponseDto mapToResponse(Item item) {
        List<ItemOptionDto> options = item.getItemOptions().stream()
                .map(option -> ItemOptionDto.builder()
                        .id(option.getId())
                        .sizeLabel(option.getSizeLabel())
                        .stockQuantity(option.getStockQuantity())
                        .build())
                .toList();

        return ItemResponseDto.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .description(item.getDescription())
                .price(item.getPrice())
                .discountPrice(item.getDiscountPrice())
                .brand(item.getBrand())
                .isSelling(item.isSelling())
                .viewCount(item.getViewCount())
                .imageUrl(item.getImageUrl())
                .category(item.getCategory())
                .averageRating(null) // TODO: 평균 별점 계산 연동 예정
                .options(options)
                .build();
    }

    private void updateImageIfPresent(Item item, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            item.updateImageUrl(imageUrl);
        }
    }

    private void updateItemFields(Item item, ItemUpdateDto dto) {
        String updatedName = dto.getItemName() != null ? dto.getItemName() : item.getItemName();
        String updatedDescription = dto.getDescription() != null ? dto.getDescription() : item.getDescription();
        int updatedPrice = dto.getPrice() != null ? dto.getPrice() : item.getPrice();
        ItemCategory updatedCategory = dto.getCategory() != null ? dto.getCategory() : item.getCategory();
        String updatedBrand = dto.getBrand() != null ? dto.getBrand() : item.getBrand();
        boolean updatedIsSelling = dto.getIsSelling() != null ? dto.getIsSelling() : item.isSelling();

        item.updateItemDetails(updatedName, updatedDescription, updatedPrice, updatedCategory, updatedBrand, updatedIsSelling);

        if (dto.getDiscountPrice() != null) {
            item.applyDiscountPrice(dto.getDiscountPrice());
        }
    }

    /**
     * TODO :
     * 현재는 옵션을 전체 교체하는 방식으로 처리하고 있음.
     * 향후 관리자 페이지에서 옵션을 개별 수정/삭제/추가할 수 있게 되면,
     * ID 기반으로 옵션을 비교하여 변경 분기 로직으로 확장 필요
     */
    private void replaceItemOptions(Item item, List<ItemOptionDto> newOptions) {
        item.getItemOptions().clear();

        for (ItemOptionDto optionDto : newOptions) {
            ItemOption newOption = ItemOption.create(item, optionDto.getSizeLabel(), optionDto.getStockQuantity());
            item.addItemOption(newOption);
        }
    }

    private String saveImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return properties.getUrlPrefix() + "item/" + DEFAULT_IMAGE_NAME;
        }
        return fileStorageService.store(imageFile, "item");
    }
}