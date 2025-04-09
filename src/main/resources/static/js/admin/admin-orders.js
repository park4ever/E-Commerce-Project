document.addEventListener("DOMContentLoaded", function () {
    addSortChangeListener();
    addHeaderSortListeners();
    loadOrders();
});

let currentPage = 0;
const pageSize = 10;
let currentSortField = "orderDate";
let currentSortDirection = "desc";

function loadOrders(page = 0) {
    currentPage = page;

    const searchKeyword = document.getElementById("searchKeyword")?.value || "";
    const searchField = document.getElementById("searchField")?.value || "all";

    const params = new URLSearchParams({
        searchKeyword,
        searchField,
        page,
        size: pageSize,
        sortBy: `${currentSortField},${currentSortDirection}`,
    });

    fetch(`/api/admin/orders?${params.toString()}`)
        .then(response => response.json())
        .then(data => {
            renderOrders(data.content);
            renderPagination(data.totalPages, data.number);
            updateHeaderSortIndicators();
        })
        .catch(error => console.error("주문 목록 로딩 실패:", error));
}

function renderOrders(orders) {
    const tbody = document.getElementById("orders-table-body");
    tbody.innerHTML = "";

    if (orders.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9">주문 내역이 없습니다.</td></tr>`;
        return;
    }

    orders.forEach(order => {
        const tr = document.createElement("tr");

        const orderItems = order.orderItems;
        const totalQuantity = orderItems.reduce((sum, item) => sum + item.count, 0);
        const allItemNames = orderItems.map(item => item.itemName).join(", ");
        const itemSummary = (orderItems.length === 1)
            ? orderItems[0].itemName
            : `${orderItems[0].itemName} 외 ${orderItems.length - 1}개`;

        tr.innerHTML = `
            <td>${order.id}</td>
            <td title="${allItemNames}">${itemSummary}</td>
            <td>${totalQuantity}</td>
            <td>${order.memberName}</td>
            <td>${formatDate(order.orderDate)}</td>
            <td>${order.orderStatus}</td>
            <td>${order.totalAmount.toLocaleString()}원</td>
            <td>${order.isPaid ? '결제 완료' : '미결제'}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="goToOrderDetail(${order.id})">상세 보기</button>
            </td>
        `;

        tbody.appendChild(tr);
    });
}

function renderPagination(totalPages, currentPage) {
    const container = document.getElementById("pagination-container");
    container.innerHTML = "";

    for (let i = 0; i < totalPages; i++) {
        const btn = document.createElement("button");
        btn.className = `btn btn-sm ${i === currentPage ? 'btn-primary' : 'btn-outline-secondary'} mx-1`;
        btn.textContent = i + 1;
        btn.onclick = () => loadOrders(i);
        container.appendChild(btn);
    }
}

function goToOrderDetail(orderId) {
    window.location.href = `/admin/orders/${orderId}`;
}

function formatDate(dateTimeStr) {
    const date = new Date(dateTimeStr);
    return date.toLocaleString("ko-KR");
}

// 정렬 드롭다운 변경 시 바로 반영
function addSortChangeListener() {
    const sortSelect = document.getElementById("sortSelect");
    if (sortSelect) {
        sortSelect.addEventListener("change", () => {
            const [field, dir] = sortSelect.value.split(",");
            currentSortField = field;
            currentSortDirection = dir;
            loadOrders();
        });
    }
}

// 테이블 헤더 클릭 시 정렬
function addHeaderSortListeners() {
    const headers = document.querySelectorAll("th[data-sort]");
    headers.forEach(th => {
        th.style.cursor = "pointer";
        th.addEventListener("click", () => {
            const field = th.dataset.sort;

            if (currentSortField === field) {
                currentSortDirection = currentSortDirection === "asc" ? "desc" : "asc";
            } else {
                currentSortField = field;
                currentSortDirection = "asc";
            }

            loadOrders();
        });
    });
}

// 헤더에 정렬 방향 표시 (▲ ▼)
function updateHeaderSortIndicators() {
    const headers = document.querySelectorAll("th[data-sort]");
    headers.forEach(th => {
        const field = th.dataset.sort;
        let baseText = th.textContent.replace(/[\u25B2\u25BC]/g, "").trim(); // 기존 ▲▼ 제거
        if (field === currentSortField) {
            const arrow = currentSortDirection === "asc" ? " ▲" : " ▼";
            th.innerHTML = `${baseText}${arrow}`;
        } else {
            th.innerHTML = baseText;
        }
    });
}