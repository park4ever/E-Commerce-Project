package platform.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.ecommerce.dto.item.ItemResponseDto;
import platform.ecommerce.service.ItemService;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemApiController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemResponseDto getItem(@PathVariable("id") Long id) {
        return itemService.findItem(id);
    }
}
