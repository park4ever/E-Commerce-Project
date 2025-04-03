document.addEventListener("DOMContentLoaded", function () {
    const itemId = window.location.pathname.split("/").pop();
    const detailContainer = document.getElementById("item-detail");
    const editModalElement = document.getElementById("editItemModal");
    const editModal = new bootstrap.Modal(editModalElement);

    const saveBtn = document.getElementById("saveItemBtn");

    fetch(`/api/admin/items/${itemId}`)
        .then(res => res.json())
        .then(item => {
            const isActive = item.isAvailable ? '✅ 판매중' : '❌ 품절';

            // 상세 정보 렌더링
            detailContainer.innerHTML = `
                <div class="row">
                    <div class="col-md-4">
                        <img src="${item.imageUrl.startsWith('/') ? item.imageUrl : '/images/' + item.imageUrl}" class="img-fluid" alt="상품 이미지">
                    </div>
                    <div class="col-md-8">
                        <h4>${item.itemName}</h4>
                        <p>${item.description}</p>
                        <p><strong>가격:</strong> ${item.price}원</p>
                        <p><strong>재고:</strong> ${item.stockQuantity}개</p>
                        <p><strong>상태:</strong> ${isActive}</p>
                        <p><strong>등록일:</strong> ${new Date(item.createdDate).toLocaleString()}</p>
                    </div>
                </div>
            `;

            // 수정 버튼 클릭 → 모달 열기 + 값 세팅
            const editBtn = document.getElementById("edit-btn");
            if (editBtn) {
                editBtn.addEventListener("click", function () {
                    document.getElementById("edit-name").value = item.itemName;
                    document.getElementById("edit-desc").value = item.description;
                    document.getElementById("edit-price").value = item.price;
                    document.getElementById("edit-stock").value = item.stockQuantity;
                    editModal.show();
                });
            }

            // 상태 토글
            const toggleBtn = document.getElementById("toggle-btn");
            if (toggleBtn) {
                toggleBtn.textContent = item.isAvailable ? "비활성화" : "활성화";
                toggleBtn.classList.toggle("btn-secondary", item.isAvailable);
                toggleBtn.classList.toggle("btn-success", !item.isAvailable);

                toggleBtn.onclick = () => {
                    const action = item.isAvailable ? "deactivate" : "activate";
                    fetch(`/api/admin/items/${itemId}/${action}`, { method: "PUT" })
                        .then(() => location.reload())
                        .catch(err => alert("상태 변경 실패: " + err.message));
                };
            }

            // 삭제 버튼
            const deleteBtn = document.getElementById("delete-btn");
            if (deleteBtn) {
                deleteBtn.onclick = () => {
                    if (!confirm("정말 삭제하시겠습니까?")) return;
                    fetch(`/api/admin/items/${itemId}`, { method: "DELETE" })
                        .then(() => {
                            alert("삭제 완료!");
                            location.href = "/admin/items";
                        })
                        .catch(err => alert("삭제 실패: " + err.message));
                };
            }
        })
        .catch(err => {
            console.error("🚨 상품 불러오기 실패:", err);
            detailContainer.innerHTML = `<p class="text-danger">상품 정보를 불러오는 데 실패했습니다.</p>`;
        });

    // 저장 버튼 이벤트
    if (saveBtn) {
        saveBtn.addEventListener("click", function () {
            const updatedItem = {
                itemName: document.getElementById("edit-name").value,
                description: document.getElementById("edit-desc").value,
                price: parseInt(document.getElementById("edit-price").value),
                stockQuantity: parseInt(document.getElementById("edit-stock").value)
            };

            fetch(`/api/admin/items/${itemId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updatedItem)
            })
                .then(res => {
                    if (!res.ok) throw new Error("수정 실패");
                    return res.json();
                })
                .then(() => {
                    alert("상품 정보가 수정되었습니다.");
                    location.reload();
                })
                .catch(err => alert("🚨 수정 실패: " + err.message));
        });
    }
});
