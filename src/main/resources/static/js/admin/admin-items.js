document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ 상품 관리 페이지 로드 완료");

    let currentSort = { property: "createdDate", direction: "DESC" };

    // 상품 목록 로드
    window.loadItems = function (page = 0) {
        const keyword = document.getElementById("searchKeyword").value || "";
        const searchField = document.getElementById("searchField").value || "all";
        const category = document.getElementById("categoryFilter")?.value || "";
        const sortValue = document.getElementById("sortSelect")?.value || `${currentSort.property},${currentSort.direction}`;
        const [sortProperty, sortDirectionRaw] = sortValue.split(",");
        const sortDirection = sortDirectionRaw.toUpperCase();

        const priceMin = document.getElementById("priceMin").value || "";
        const priceMax = document.getElementById("priceMax").value || "";
        const stockMin = document.getElementById("stockMin").value || "";
        const stockMax = document.getElementById("stockMax").value || "";

        if (priceMin && priceMax && parseInt(priceMin) > parseInt(priceMax)) {
            alert("최소 가격은 최대 가격보다 작거나 같아야 합니다.");
            return;
        }
        if (stockMin && stockMax && parseInt(stockMin) > parseInt(stockMax)) {
            alert("최소 재고는 최대 재고보다 작거나 같아야 합니다.");
            return;
        }

        currentSort = { property: sortProperty, direction: sortDirection };

        let url = `/api/admin/items?searchKeyword=${keyword}&searchField=${searchField}&page=${page}&sortBy=${sortProperty}&direction=${sortDirection}`;
        if (category) url += `&category=${category}`;
        if (priceMin) url += `&priceMin=${priceMin}`;
        if (priceMax) url += `&priceMax=${priceMax}`;
        if (stockMin) url += `&stockMin=${stockMin}`;
        if (stockMax) url += `&stockMax=${stockMax}`;

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    return response.json().then(error => {
                        alert(error.error);
                        throw new Error(error.error);
                    });
                }
                return response.json();
            })
            .then(renderTable)
            .catch(error => console.error("🚨 상품 목록 요청 실패:", error));
    };

    // 테이블 렌더링
    function renderTable(data) {
        const tableBody = document.getElementById("items-table-body");
        tableBody.innerHTML = "";

        data.content.forEach(item => {
            const isActive = item.isAvailable ? "✅ 판매중" : "❌ 품절";

            const discountHtml = item.discountPrice
                ? `<span style='color:red;'>${formatCurrency(item.discountPrice)}</span><br><span style='text-decoration:line-through; color:gray;'>${formatCurrency(item.price)}</span>`
                : "-";

            const row = `
                <tr data-item-id="${item.id}">
                    <td><img src="${item.imageUrl}" alt="상품 이미지" style="width: 60px; height: 60px; object-fit: cover;"></td>
                    <td>${item.id}</td>
                    <td><a href="/admin/items/${item.id}" class="item-link">${item.itemName}</a></td>
                    <td>${item.category || "-"}</td>
                    <td>${formatCurrency(item.price)}</td>
                    <td>${discountHtml}</td>
                    <td>${item.stockQuantity !== undefined ? item.stockQuantity : "-"}</td>
                    <td>${formatDate(item.createdDate)}</td>
                    <td>${item.totalSales ?? 0}</td>
                    <td>${isActive}</td>
                    <td>
                        <a href="/admin/items/${item.id}" class="btn btn-sm btn-info">상세</a>
                        <a href="/admin/items/edit/${item.id}" class="btn btn-sm btn-warning">수정</a>
                    </td>
                </tr>
            `;
            tableBody.insertAdjacentHTML("beforeend", row);
        });

        document.querySelectorAll(".item-link").forEach(link => {
            link.addEventListener("click", function (event) {
                event.preventDefault();
                window.location.href = this.getAttribute("href");
            });
        });

        renderPagination(data);
    }

    function renderPagination(data) {
        const paginationContainer = document.getElementById("pagination-container");
        paginationContainer.innerHTML = "";

        if (data.totalPages > 1) {
            let paginationHtml = `<ul class="pagination">`;
            for (let i = 0; i < data.totalPages; i++) {
                paginationHtml += `
                    <li class="page-item ${i === data.number ? "active" : ""}">
                        <button class="page-link" data-page="${i}">${i + 1}</button>
                    </li>`;
            }
            paginationHtml += `</ul>`;
            paginationContainer.innerHTML = paginationHtml;

            paginationContainer.querySelectorAll("button.page-link").forEach(button => {
                button.addEventListener("click", function () {
                    loadItems(parseInt(this.dataset.page));
                });
            });
        }
    }

    function formatCurrency(amount) {
        return amount?.toLocaleString("ko-KR") + "원";
    }

    function formatDate(dateString) {
        if (!dateString) return "-";
        const date = new Date(dateString);
        return date.toLocaleDateString("ko-KR", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit"
        });
    }

    // 이벤트 바인딩
    document.getElementById("searchKeyword").addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            event.preventDefault();
            loadItems();
        }
    });

    document.getElementById("sortSelect")?.addEventListener("change", () => loadItems());

    document.querySelectorAll("th[data-sort]").forEach(th => {
        th.addEventListener("click", function () {
            const field = this.dataset.sort;
            const newDirection = (currentSort.property === field && currentSort.direction === "ASC") ? "DESC" : "ASC";
            currentSort = { property: field, direction: newDirection };
            const sortSelect = document.getElementById("sortSelect");
            if (sortSelect) sortSelect.value = `${field},${newDirection}`;
            loadItems();
        });
    });

    document.querySelector("form").addEventListener("submit", function (event) {
        event.preventDefault();
        loadItems();
    });

    loadItems(); // 초기 로드
});