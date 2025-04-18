package platform.ecommerce.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.admin.AdminItemDto;
import platform.ecommerce.dto.item.ItemPageRequestDto;
import platform.ecommerce.service.AdminService;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.springframework.data.domain.Sort.Direction.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/items")
public class AdminItemApiController {

    private final AdminService adminService;

    /**
     * 상품 목록 조회(검색 및 페이징 포함)
     */
    @GetMapping
    public ResponseEntity<?> getAllItems(
            @Valid ItemPageRequestDto requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(joining("\n"));

            return ResponseEntity.badRequest().body(Map.of("error", errorMessages));
        }

        Page<AdminItemDto> items = adminService.getAllItems(requestDto);

        return ResponseEntity.ok(items);
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminItemDto> getItemById(@PathVariable("id") Long id) {
        AdminItemDto item = adminService.getItemById(id);

        return ResponseEntity.ok(item);
    }

    /**
     * 상품 정보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdminItemDto> updateItem(@PathVariable("id") Long id,
                                           @RequestBody @Valid AdminItemDto dto) {
        adminService.updateItem(id, dto);
        AdminItemDto updatedItem = adminService.getItemById(id);

        return ResponseEntity.ok(updatedItem);
    }

    /**
     * 상품 비활성화
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateItem(@PathVariable("id") Long id) {
        adminService.toggleItemAvailability(id, false);

        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 활성화
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateItem(@PathVariable("id") Long id) {
        adminService.toggleItemAvailability(id, true);

        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Long id) {
        adminService.deleteItem(id);

        return ResponseEntity.noContent().build();
    }
}
