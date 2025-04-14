const orderId = window.location.pathname.split("/").pop();
let latestOrderItems = [];

window.addEventListener("DOMContentLoaded", () => {
    loadOrderDetail();

    document.getElementById("saveOrderBtn").addEventListener("click", updateOrder);
    document.getElementById("cancelOrderBtn").addEventListener("click", cancelOrder);

    document.getElementById("editOrderBtn").addEventListener("click", () => {
        const modal = new bootstrap.Modal(document.getElementById("editOrderModal"));
        modal.show();
    });

    document.getElementById("saveOrderItemBtn").addEventListener("click", saveOrderItem);
    document.getElementById("deleteOrderItemBtn").addEventListener("click", deleteOrderItem);
});

function loadOrderDetail() {
    fetch(`/api/admin/orders/${orderId}`)
        .then(response => response.json())
        .then(order => {
            document.getElementById("order-id").textContent = order.id;
            document.getElementById("member-name").textContent = order.memberName;
            document.getElementById("member-email").textContent = order.memberEmail;
            document.getElementById("order-date").textContent = formatDate(order.orderDate);
            document.getElementById("order-status").textContent = order.orderStatus;
            document.getElementById("payment-method").textContent = order.paymentMethod;
            document.getElementById("is-paid").textContent = order.isPaid ? "결제 완료" : "미결제";
            document.getElementById("total-amount").textContent = `${(order.totalAmount ?? 0).toLocaleString()}원`;
            document.getElementById("shipping-address").textContent =
                `${order.zipcode} ${order.city} ${order.street} ${order.additionalInfo || ""}`.trim();
            document.getElementById("last-modified").textContent = order.modifiedDate ? formatDate(order.modifiedDate) : "-";

            document.getElementById("zipcode").value = order.zipcode || "";
            document.getElementById("city").value = order.city || "";
            document.getElementById("street").value = order.street || "";
            document.getElementById("additionalInfo").value = order.additionalInfo || "";

            document.getElementById("edit-order-status").value = order.orderStatus;
            document.getElementById("edit-reason").value = order.modificationReason || "";

            renderOrderItems(order.orderItems);
        })
        .catch(err => console.error("주문 상세 조회 실패:", err));
}

function renderOrderItems(items) {
    const tbody = document.getElementById("order-items-table-body");
    tbody.innerHTML = "";
    latestOrderItems = items;

    if (!items || items.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7">주문 상품이 없습니다.</td></tr>`;
        return;
    }

    items.forEach(item => {
        const tr = document.createElement("tr");

        const imgSrc = item.imageUrl && !item.imageUrl.startsWith("/")
            ? `/images/${item.imageUrl}`
            : item.imageUrl || "/images/default.png";

        tr.innerHTML = `
            <td>${item.itemId}</td>
            <td><img src="${imgSrc}" alt="상품 이미지" width="60" style="object-fit: cover;"></td>
            <td>${item.itemName}</td>
            <td>${(item.orderPrice ?? 0).toLocaleString()}원</td>
            <td>${item.count}</td>
            <td>${((item.orderPrice ?? 0) * (item.count ?? 0)).toLocaleString()}원</td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="openEditItemModal(${item.orderItemId}, ${item.count}, ${item.orderPrice})">수정</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function openEditItemModal(orderItemId, quantity, price) {
    document.getElementById("edit-item-id").value = orderItemId;
    document.getElementById("edit-item-quantity").value = quantity;
    document.getElementById("edit-item-price").value = price;

    const modal = new bootstrap.Modal(document.getElementById("editOrderItemModal"));
    modal.show();
}

function saveOrderItem() {
    const id = parseInt(document.getElementById("edit-item-id").value);
    const quantity = parseInt(document.getElementById("edit-item-quantity").value);
    const price = parseInt(document.getElementById("edit-item-price").value);

    const original = latestOrderItems.find(item => item.orderItemId === id);
    const requests = [];

    if (quantity !== original.count) {
        requests.push(fetch(`/api/admin/order-items/${id}/quantity?newQuantity=${quantity}`, { method: "PUT" }));
    }
    if (price !== original.orderPrice) {
        requests.push(fetch(`/api/admin/order-items/${id}/price?newPrice=${price}`, { method: "PUT" }));
    }

    if (requests.length === 0) {
        alert("변경된 값이 없습니다.");
        return;
    }

    Promise.all(requests)
        .then(responses => {
            if (responses.every(r => r.ok)) {
                alert("상품 수정이 완료되었습니다.");
                bootstrap.Modal.getInstance(document.getElementById("editOrderItemModal")).hide();
                loadOrderDetail();
            } else {
                throw new Error("수정 실패");
            }
        })
        .catch(() => alert("상품 수정 실패"));
}

function deleteOrderItem() {
    const id = document.getElementById("edit-item-id").value;
    if (!confirm("정말 이 상품을 삭제하시겠습니까?")) return;

    fetch(`/api/admin/order-items/${id}`, { method: "DELETE" })
        .then(response => {
            if (!response.ok) throw new Error();
            alert("상품이 삭제되었습니다.");
            bootstrap.Modal.getInstance(document.getElementById("editOrderItemModal")).hide();
            loadOrderDetail(); // 삭제 후 반영
        })
        .catch(() => alert("상품 삭제 실패"));
}

function updateOrder() {
    const newStatus = document.getElementById("edit-order-status").value;
    const zipcode = document.getElementById("zipcode").value.trim();
    const city = document.getElementById("city").value.trim();
    const street = document.getElementById("street").value.trim();
    const additionalInfo = document.getElementById("additionalInfo").value.trim();
    const reason = document.getElementById("edit-reason").value.trim();

    fetch(`/api/admin/orders/${orderId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            orderStatus: newStatus, zipcode, city, street, additionalInfo,
            modificationReason: reason
        })
    })
        .then(response => {
            if (!response.ok) throw new Error("주문 수정 실패");
            return response.text();
        })
        .then(() => {
            alert("주문이 성공적으로 수정되었습니다.");
            location.reload();
        })
        .catch(err => alert("주문 수정 실패: " + err));
}

function cancelOrder() {
    if (!confirm("정말 이 주문을 취소하시겠습니까?")) return;

    fetch(`/api/admin/orders/${orderId}`, { method: "DELETE" })
        .then(response => {
            if (!response.ok) throw new Error("주문 취소 실패");
            alert("주문이 취소되었습니다.");
            window.location.href = "/admin/orders";
        })
        .catch(err => alert("오류 발생: " + err));
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return isNaN(date) ? "-" : date.toLocaleString("ko-KR");
}

function fillCurrentShippingAddress() {
    fetch(`/api/admin/orders/${orderId}`)
        .then(response => response.json())
        .then(order => {
            document.getElementById("zipcode").value = order.zipcode || "";
            document.getElementById("city").value = order.city || "";
            document.getElementById("street").value = order.street || "";
            document.getElementById("additionalInfo").value = order.additionalInfo || "";
        })
        .catch(err => console.error("기존 배송지 불러오기 실패:", err));
}
