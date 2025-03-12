package platform.ecommerce.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import platform.ecommerce.dto.admin.AdminMemberDto;
import platform.ecommerce.service.AdminService;

import static org.springframework.data.domain.Sort.Direction.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMemberApiController {

    private final AdminService adminService;

    /**
     * 회원 목록 조회(검색 및 페이징 포함)
     */
    @GetMapping
    public ResponseEntity<Page<AdminMemberDto>> getAllMembers(
            @RequestParam(required = false) String searchKeyword,
            @PageableDefault(size = 10, sort = "createdDate", direction = DESC) Pageable pageable) {
        Page<AdminMemberDto> members = adminService.getAllMembers(searchKeyword, pageable);

        return ResponseEntity.ok(members);
    }

    /**
     * 회원 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminMemberDto> getMemberById(@PathVariable Long id) {
        AdminMemberDto member = adminService.getMemberById(id);

        return ResponseEntity.ok(member);
    }

    /**
     * 회원 정보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMember(@PathVariable Long id,
                                             @RequestBody @Valid AdminMemberDto dto) {
        adminService.updateMember(id, dto);

        return ResponseEntity.noContent().build();
    }

    /**
     * 회원 비활성화
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateMember(@PathVariable Long id) {
        adminService.deactivateMember(id);

        return ResponseEntity.noContent().build();
    }
}
