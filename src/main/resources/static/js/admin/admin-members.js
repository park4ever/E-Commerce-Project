document.addEventListener("DOMContentLoaded", function () {
    console.log("회원 관리 페이지 로드 완료!");

    let currentSort = { property: "createdDate", direction: "DESC" };

    // 회원 목록 로드
    window.loadMembers = function (page = 0) {
        let keyword = document.getElementById("searchKeyword").value || "";
        let sortValue = document.getElementById("sortSelect")?.value || `${currentSort.property},${currentSort.direction}`;
        const [sortProperty, sortDirectionRaw] = sortValue.split(",");
        const sortDirection = sortDirectionRaw.toUpperCase(); // ✅ 대문자로 변환

        currentSort = { property: sortProperty, direction: sortDirection };

        console.log(`🔎 검색어: ${keyword}, 정렬: ${sortProperty}, ${sortDirection}, 페이지: ${page}`);

        fetch(`/api/admin/members?searchKeyword=${keyword}&page=${page}&sortBy=${sortProperty}&direction=${sortDirection}&t=${Date.now()}`, {
            cache: "no-store",
            headers: { "Cache-Control": "no-cache, no-store, must-revalidate" }
        })
            .then(response => response.json())
            .then(data => {
                console.log("📌 회원 데이터:", data);

                let tableBody = document.getElementById("members-table-body");
                tableBody.innerHTML = "";

                data.content.forEach(member => {
                    let isActive = member.active ?? member.isActive;

                    let row = `<tr data-member-id="${member.id}">
                        <td>${member.id}</td>
                        <td><a href="/admin/members/${member.id}" class="member-link">${member.email}</a></td>
                        <td>${member.username}</td>
                        <td>${member.phoneNumber || '-'}</td>
                        <td>${member.dateOfBirth ? new Date(member.dateOfBirth).toLocaleDateString() : '-'}</td>
                        <td>${new Date(member.createdDate).toLocaleDateString()}</td>
                        <td class="member-status" data-member-id="${member.id}">
                            ${isActive ? '✅ 활성' : '❌ 비활성'}
                        </td>
                        <td>
                            <button class="btn btn-sm btn-success activate-btn" data-member-id="${member.id}" ${isActive ? 'disabled' : ''}>활성화</button>
                            <button class="btn btn-sm btn-danger deactivate-btn" data-member-id="${member.id}" ${!isActive ? 'disabled' : ''}>비활성화</button>
                        </td>
                    </tr>`;
                    tableBody.innerHTML += row;
                });

                // 상세 조회 페이지 이동
                document.querySelectorAll(".member-link").forEach(link => {
                    link.addEventListener("click", function (event) {
                        event.preventDefault();
                        window.location.href = this.getAttribute("href");
                    });
                });

                // 버튼 이벤트 리스너
                document.querySelectorAll(".activate-btn").forEach(button => {
                    button.addEventListener("click", function () {
                        updateMemberStatus(this.dataset.memberId, true);
                    });
                });

                document.querySelectorAll(".deactivate-btn").forEach(button => {
                    button.addEventListener("click", function () {
                        updateMemberStatus(this.dataset.memberId, false);
                    });
                });

                // ✅ 페이지네이션 렌더링
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
                            loadMembers(selectedPage);
                        });
                    });
                }
            })
            .catch(error => console.error("🚨 회원 목록 불러오기 실패:", error));
    };

    // 회원 활성/비활성화
    window.updateMemberStatus = function (memberId, isActive) {
        let url = `/api/admin/members/${memberId}/` + (isActive ? "activate" : "deactivate");

        fetch(url, {
            method: "PUT",
            headers: { "Cache-Control": "no-cache, no-store, must-revalidate" }
        })
            .then(response => {
                if (!response.ok) throw new Error("회원 상태 변경 실패");
                return response.text();
            })
            .then(() => {
                alert("회원 상태가 변경되었습니다.");

                let statusCell = document.querySelector(`.member-status[data-member-id="${memberId}"]`);
                if (statusCell) statusCell.innerHTML = isActive ? '✅ 활성' : '❌ 비활성';

                let activateBtn = document.querySelector(`.activate-btn[data-member-id="${memberId}"]`);
                let deactivateBtn = document.querySelector(`.deactivate-btn[data-member-id="${memberId}"]`);
                if (activateBtn && deactivateBtn) {
                    activateBtn.disabled = isActive;
                    deactivateBtn.disabled = !isActive;
                }

                setTimeout(() => loadMembers(), 500);
            })
            .catch(error => console.error("🚨 회원 상태 변경 실패:", error));
    };

    // 검색 (엔터)
    document.getElementById("searchKeyword").addEventListener("keypress", function (event) {
        if (event.key === "Enter") loadMembers();
    });

    // 드롭다운 정렬
    document.getElementById("sortSelect")?.addEventListener("change", function () {
        const [property, directionRaw] = this.value.split(",");
        const direction = directionRaw.toUpperCase();
        currentSort = { property, direction };
        loadMembers();
    });

    // 헤더 클릭 정렬
    document.querySelectorAll("th[data-sort]").forEach(th => {
        th.addEventListener("click", function () {
            const field = this.dataset.sort;
            const newDirection = (currentSort.property === field && currentSort.direction === "ASC") ? "DESC" : "ASC";
            currentSort = { property: field, direction: newDirection };

            const sortSelect = document.getElementById("sortSelect");
            if (sortSelect) {
                sortSelect.value = `${field},${newDirection}`;
            }

            loadMembers();
        });
    });

    loadMembers(); // 최초 호출
});