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

        // 갱신된 정렬 기준을 currentSort에 반영
        currentSort = { property: sortProperty, direction: sortDirection };

        console.log(`🔎 검색어: ${keyword}, 정렬: ${sortProperty}, ${sortDirection}, 페이지: ${page}, 검색 기준: ${searchField}`);

        fetch(`/api/admin/items?searchKeyword=${keyword}&searchField=${searchField}&page=${page}&sortBy=${sortProperty}&direction=${sortDirection}&t=${Date.now()}`, {
            cache: "no-store",
            headers: { "Cache-Control": "no-cache, no-store, must-revalidate" }
        })
            .then(response => response.json())
            .then(data => {
                console.log("📌 상품 데이터:", data);

                let tableBody = document.getElementById("items-table-body");
                tableBody.innerHTML = "";

                data.content.forEach(item => {
                    let isActive = item.isAvailable ? '✅ 판매중' : '❌ 품절';

                    let row = `<tr data-item-id="${item.id}">
                        <td>${item.id}</td>
                        <td>${item.itemName}</td>
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

                // 페이지네이션 렌더링
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
                            </li>
                        `;
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
            })
            .catch(error => console.error("🚨 상품 목록 불러오기 실패:", error));
    };

    // 검색 (엔터 키 입력 시 실행)
    document.getElementById("searchKeyword").addEventListener("keypress", function (event) {
        if (event.key === "Enter") loadItems();
    });

    // 드롭다운 정렬
    document.getElementById("sortSelect")?.addEventListener("change", function () {
        const [property, directionRaw] = this.value.split(",");
        const direction = directionRaw.toUpperCase();
        currentSort = { property, direction };
        loadItems();
    });

    // 테이블 헤더 정렬
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

    loadItems();
});