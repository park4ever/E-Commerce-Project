document.addEventListener("DOMContentLoaded", function () {
    console.log("회원 관리 페이지 로드 완료!");

    // 회원 목록 로드
    window.loadMembers = function () {
        let keyword = document.getElementById("searchKeyword").value || "";
        console.log(`🔎 검색어: ${keyword}`);

        fetch(`/api/admin/members?searchKeyword=${keyword}&t=${Date.now()}`, {
            cache: "no-store",
            headers: { "Cache-Control": "no-cache, no-store, must-revalidate" }
        })
            .then(response => response.json())
            .then(data => {
                console.log("📌 회원 데이터:", data);

                let tableBody = document.getElementById("members-table-body");
                tableBody.innerHTML = "";  // 기존 데이터 초기화

                data.content.forEach(member => {
                    let isActive = member.active ?? member.isActive;

                    let row = `<tr data-member-id="${member.id}">
                        <td>${member.id}</td>
                        <td><a href="/admin/members/${member.id}" class="member-link">${member.email}</a></td> <!-- ✅ 상세 조회 페이지 이동 -->
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

                // 회원 상세 조회 페이지 이동
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
                if (!response.ok) {
                    throw new Error("회원 상태 변경 실패");
                }
                return response.text();
            })
            .then(() => {
                alert(`회원 상태가 변경되었습니다.`);

                // UI 즉시 변경
                let statusCell = document.querySelector(`.member-status[data-member-id="${memberId}"]`);
                if (statusCell) {
                    statusCell.innerHTML = isActive ? '✅ 활성' : '❌ 비활성';
                }

                // 버튼 비활성화 처리
                let activateBtn = document.querySelector(`.activate-btn[data-member-id="${memberId}"]`);
                let deactivateBtn = document.querySelector(`.deactivate-btn[data-member-id="${memberId}"]`);

                if (activateBtn && deactivateBtn) {
                    activateBtn.disabled = isActive;
                    deactivateBtn.disabled = !isActive;
                }

                // 최신 데이터 갱신 (0.5초 후)
                setTimeout(loadMembers, 500);
            })
            .catch(error => console.error("🚨 회원 상태 변경 실패:", error));
    };

    // 검색 기능 (Enter 키 입력 시 실행)
    document.getElementById("searchKeyword").addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            loadMembers();
        }
    });

    loadMembers();  // 페이지 로드 시 자동 실행
});