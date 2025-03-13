document.addEventListener("DOMContentLoaded", function () {
    console.log("대시보드 페이지 로드 완료!");

    function loadDashboardData() {
        fetch("/api/admin/dashboard")
            .then(response => response.json())
            .then(data => {
                document.getElementById("total-members").innerText = data.totalMembers + "명";
                document.getElementById("new-members-7days").innerText = data.newMembers7Days + "명";
                document.getElementById("new-members-30days").innerText = data.newMembers30Days + "명";

                document.getElementById("total-items").innerText = data.totalItems + "개";

                document.getElementById("total-orders").innerText = data.totalOrders + "건";
                document.getElementById("recent-orders-7days").innerText = data.recentOrders7Days + "건";
                document.getElementById("total-revenue").innerText = data.totalRevenue.toLocaleString() + "원";
                document.getElementById("recent-revenue-30days").innerText = data.recentRevenue30Days.toLocaleString() + "원";

                document.getElementById("total-reviews").innerText = data.totalReviews + "개";
                document.getElementById("recent-reviews-30days").innerText = data.recentReviews30Days + "개";
            })
            .catch(error => console.error("대시보드 데이터 로딩 실패:", error));
    }

    if (document.getElementById("total-members")) {
        loadDashboardData();
    }
});
