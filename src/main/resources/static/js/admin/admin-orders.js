document.addEventListener("DOMContentLoaded", function () {
    loadOrders();
});

let currentPage = 0;
const pageSize = 10;

function loadOrders(page = 0) {
    currentPage = page;

    const searchKeyword = document.getElementById("searchKeyword")?.value || "";
    const sortValue = document.getElementById("sortSelect")?.value || "orderDate,desc";
    const [sortField, sortDirection] = sortValue.split(",");

    const url = `/api/admin/orders?searchKeyword=${encodeURIComponent(searchKeyword)}&page=${page}&size=${pageSize}&sort=${sortField},${sortDirection}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            renderOrders(data.content);
            renderPagination(data.totalPages, data.number);
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

        // 대표 상품명 요약 방식 적용
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
            <td>${order.paid ? '결제 완료' : '미결제'}</td>
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
