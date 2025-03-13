package platform.ecommerce.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.ecommerce.service.AdminService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardApiController {

    private final AdminService adminService;

    /**
     * 관리자 대시보드 통계 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        HashMap<String, Object> dashboardData = new HashMap<>();

        dashboardData.put("totalMembers", adminService.countMembers());
        dashboardData.put("newMembers7Days", adminService.countNewMembers(7));
        dashboardData.put("newMembers30Days", adminService.countNewMembers(30));

        dashboardData.put("totalItems", adminService.countItems());

        dashboardData.put("totalOrders", adminService.countOrders());
        dashboardData.put("recentOrders7Days", adminService.countRecentOrders(7));
        dashboardData.put("totalRevenue", adminService.calculateTotalRevenue());
        dashboardData.put("recentRevenue30Days", adminService.calculateRecentRevenue(30));

        dashboardData.put("totalReviews", adminService.countReviews());
        dashboardData.put("recentReviews30Days", adminService.countRecentReviews(30));

        return ResponseEntity.ok(dashboardData);
    }
}
