document.addEventListener("DOMContentLoaded", function () {
    console.log("상품 관리 페이지 로드 완료!");

    let currentSort = { property: "createdDate", direction: "desc" };

    // 상품 목록 로드
    window.loadItems = function (page = 0) {
        let keyword = document.getElementById("searchKeyword").value || "";
        let searchField = document.getElementById("searchField").value || "all";
        let sortValue = document.getElementById("sortSelect")?.value || `${currentSort.property},${currentSort.direction}`;
        const [sortProperty, sortDirectionRaw] = sortValue.split(",");
        const sortDirection = sortDirectionRaw.toUpperCase();

        // 가격 및 재고 범위 값 가져오기
        const priceMin = document.getElementById("priceMin").value || null;
        const priceMax = document.getElementById("priceMax").value || null;
        const stockMin = document.getElementById("stockMin").value || null;
        const stockMax = document.getElementById("stockMax").value || null;

        // 프론트 단 유효성 검사
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

        if (priceMin) url += `&priceMin=${priceMin}`;
        if (priceMax) url += `&priceMax=${priceMax}`;
        if (stockMin) url += `&stockMin=${stockMin}`;
        if (stockMax) url += `&stockMax=${stockMax}`;

        fetch(url, {
            cache: "no-store",
            headers: { "Cache-Control": "no-cache, no-store, must-revalidate" }
        })
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
            .catch(error => console.error("🚨 상품 목록 불러오기 실패:", error));
    };

    // 테이블 렌더링 함수 분리
    function renderTable(data) {
        let tableBody = document.getElementById("items-table-body");
        tableBody.innerHTML = "";

        data.content.forEach(item => {
            let isActive = item.isAvailable ? '✅ 판매중' : '❌ 품절';

            let row = `<tr data-item-id="${item.id}">
                <td><img src="${item.imageUrl}" alt="상품 이미지" style="width: 60px; height: 60px; object-fit: cover;"></td>
                <td>${item.id}</td>
                <td><a href="/admin/items/${item.id}" class="item-link">${item.itemName}</a></td>
                <td>${item.price}</td>
                <td>${item.stockQuantity}</td>
                <td>${new Date(item.createdDate).toLocaleDateString()}</td>
                <td>${item.totalSales}</td>
                <td>${isActive}</td>
                <td>
                    <a href="/admin/items/${item.id}" class="btn btn-sm btn-info">상세</a>
                    <a href="/admin/items/edit/${item.id}" class="btn btn-sm btn-warning">수정</a>
                </td>
            </tr>`;
            tableBody.innerHTML += row;
        });

        document.querySelectorAll(".item-link").forEach(link => {
            link.addEventListener("click", function (event) {
                event.preventDefault();
                window.location.href = this.getAttribute("href");
            });
        });

        const paginationContainer = document.getElementById("pagination-container");
        paginationContainer.innerHTML = "";

        const totalPages = data.totalPages;
        const currentPage = data.number;

        if (totalPages > 1) {
            let paginationHtml = `<ul class="pagination">`;

            for (let i = 0; i < totalPages; i++) {
                paginationHtml += `
                    <li class="page-item ${i === currentPage ? 'active' : ''}">
                        <button class="page-link" data-page="${i}">${i + 1}</button>
                    </li>`;
            }

            paginationHtml += `</ul>`;
            paginationContainer.innerHTML = paginationHtml;

            paginationContainer.querySelectorAll("button.page-link").forEach(button => {
                button.addEventListener("click", function () {
                    const selectedPage = parseInt(this.dataset.page);
                    loadItems(selectedPage);
                });
            });
        }
    }

    document.getElementById("searchKeyword").addEventListener("keypress", function (event) {
        if (event.key === "Enter") loadItems();
    });

    document.getElementById("sortSelect")?.addEventListener("change", function () {
        const [property, directionRaw] = this.value.split(",");
        const direction = directionRaw.toUpperCase();
        currentSort = { property, direction };
        loadItems();
    });

    document.querySelectorAll("th[data-sort]").forEach(th => {
        th.addEventListener("click", function () {
            const field = this.dataset.sort;
            const newDirection = (currentSort.property === field && currentSort.direction === "ASC") ? "DESC" : "ASC";
            currentSort = { property: field, direction: newDirection };

            const sortSelect = document.getElementById("sortSelect");
            if (sortSelect) {
                sortSelect.value = `${field},${newDirection}`;
            }

            loadItems();
        });
    });

    document.querySelector("form").addEventListener("submit", function (event) {
        event.preventDefault();
        loadItems();
    });

    loadItems();
});