document.addEventListener("DOMContentLoaded", function () {
    console.log("회원 관리 페이지 로드 완료!");

    window.loadMembers = function () {
        let keyword = document.getElementById("searchKeyword").value || "";
        console.log(`🔎 검색어: ${keyword}`);

        fetch(`/api/admin/members?searchKeyword=${keyword}`, { cache: "no-store" }) // 항상 최신 데이터 요청
            .then(response => {
                console.log("✅ API 응답 수신:", response);
                return response.json();
            })
            .then(data => {
                console.log("📌 회원 데이터:", data);

                let tableBody = document.getElementById("members-table-body");
                tableBody.innerHTML = "";  // 기존 데이터 초기화

                data.content.forEach(member => {
                    let row = `<tr>
                        <td>${member.id}</td>
                        <td>${member.email}</td>
                        <td>${member.username}</td>
                        <td>${member.phoneNumber || '-'}</td>
                        <td>${member.dateOfBirth ? new Date(member.dateOfBirth).toLocaleDateString() : '-'}</td>
                        <td>${new Date(member.createdDate).toLocaleDateString()}</td>
                        <td>${member.isActive ? '✅ 활성' : '❌ 비활성'}</td>
                        <td>
                            <button class="btn btn-sm btn-success" onclick="updateMemberStatus(${member.id}, true)">활성화</button>
                            <button class="btn btn-sm btn-danger" onclick="updateMemberStatus(${member.id}, false)">비활성화</button>
                        </td>
                    </tr>`;
                    tableBody.innerHTML += row;
                });
            })
            .catch(error => console.error("🚨 회원 목록 불러오기 실패:", error));
    };

    window.updateMemberStatus = function (memberId, isActive) {
        let url = `/api/admin/members/${memberId}/` + (isActive ? "activate" : "deactivate");

        fetch(url, { method: "PUT" })
            .then(response => {
                if (!response.ok) {
                    throw new Error("회원 상태 변경 실패");
                }
                return response.text(); // JSON 반환이 없을 경우, 응답 본문을 무시
            })
            .then(() => {
                alert(`회원 상태가 변경되었습니다.`);
                loadMembers();  // 변경 후 목록 새로고침
            })
            .catch(error => console.error("🚨 회원 상태 변경 실패:", error));
    };

    document.getElementById("searchKeyword").addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            loadMembers();
        }
    });

    loadMembers();  // 페이지 로드 시 자동 실행
});
