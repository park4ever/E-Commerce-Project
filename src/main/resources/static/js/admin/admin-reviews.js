document.addEventListener("DOMContentLoaded", function () {
    addSortChangeListener();
    addHeaderSortListeners();
    loadReviews();
});

let currentPage = 0;
const pageSize = 10;
let currentSortField = "createdDate";
let currentSortDirection = "desc";

function loadReviews(page = 0) {
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

    fetch(`/api/admin/reviews?${params.toString()}`)
        .then(response => response.json())
        .then(data => {
            renderReviews(data.content);
            renderPagination(data.totalPages, data.number);
            updateHeaderSortIndicators();
        })
        .catch(error => console.error("리뷰 목록 로딩 실패:", error));
}

function renderReviews(reviews) {
    const tbody = document.getElementById("reviews-table-body");
    tbody.innerHTML = "";

    if (reviews.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7">등록된 리뷰가 없습니다.</td></tr>`;
        return;
    }

    reviews.forEach(review => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${review.id}</td>
            <td>${review.itemName}</td>
            <td>${review.memberName}</td>
            <td>${review.memberEmail}</td>
            <td>${"⭐".repeat(review.rating)}</td>
            <td>${review.content}</td>
            <td>${formatDate(review.createdDate)}</td>
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
        btn.onclick = () => loadReviews(i);
        container.appendChild(btn);
    }
}

function formatDate(dateTimeStr) {
    const date = new Date(dateTimeStr);
    return date.toLocaleString("ko-KR");
}

// 드롭다운 정렬 옵션 변경 시 적용
function addSortChangeListener() {
    const sortSelect = document.getElementById("sortSelect");
    if (sortSelect) {
        sortSelect.addEventListener("change", () => {
            const [field, dir] = sortSelect.value.split(",");
            currentSortField = field;
            currentSortDirection = dir;
            loadReviews();
        });
    }
}

// 헤더 클릭 정렬
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

            loadReviews();
        });
    });
}

// 헤더에 ▲▼ 표시
function updateHeaderSortIndicators() {
    const headers = document.querySelectorAll("th[data-sort]");
    headers.forEach(th => {
        const field = th.dataset.sort;
        let baseText = th.textContent.replace(/[\u25B2\u25BC]/g, "").trim();

        if (field === currentSortField) {
            const arrow = currentSortDirection === "asc" ? " ▲" : " ▼";
            th.innerHTML = `${baseText}${arrow}`;
        } else {
            th.innerHTML = baseText;
        }
    });
}
