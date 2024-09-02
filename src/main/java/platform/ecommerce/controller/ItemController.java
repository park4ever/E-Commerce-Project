package platform.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.ItemResponseDto;
import platform.ecommerce.dto.ItemSaveRequestDto;
import platform.ecommerce.dto.ItemUpdateDto;
import platform.ecommerce.service.ItemService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/new")
    public String itemForm(Model model) {
        model.addAttribute("itemSaveRequestDto", new ItemSaveRequestDto());
        return "/pages/item/itemForm";
    }

    @PostMapping("/new")
    public String registration(@Valid ItemSaveRequestDto itemSaveRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/pages/item/itemForm";
        }

        itemService.saveItem(itemSaveRequestDto);

        return "redirect:/";
    }

    @GetMapping("/list")
    public String itemList(Model model) {
        List<ItemResponseDto> items = itemService.findItems();
        model.addAttribute("items", items);
        return "/pages/item/itemList";
    }

    @GetMapping("/{id}")
    public String viewItem(@PathVariable("id") Long id, Model model) {
        ItemResponseDto item = itemService.findItem(id);
        model.addAttribute("item", item);
        return "/pages/item/itemDetail";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        ItemResponseDto itemResponseDto = itemService.findItem(id);
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.from(itemResponseDto);
        model.addAttribute("item", itemUpdateDto);
        model.addAttribute("id", id); // ID를 모델에 추가하여 폼에 전달

        return "/pages/item/itemEditForm";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("item") ItemUpdateDto itemUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/pages/item/itemEditForm";
        }

        itemService.updateItem(id, itemUpdateDto);

        return "redirect:/item/list";
    }
}
