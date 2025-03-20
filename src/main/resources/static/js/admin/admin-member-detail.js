document.addEventListener("DOMContentLoaded", function () {
    const memberId = window.location.pathname.split("/").pop();
    const editMemberBtn = document.getElementById("editMemberBtn");
    const saveMemberBtn = document.getElementById("saveMemberBtn");
    const closeModalBtn = document.querySelector("[data-bs-dismiss='modal']"); // ✅ 닫기 버튼
    const editMemberModal = new bootstrap.Modal(document.getElementById("editMemberModal")); // ✅ Bootstrap 모달 초기화

    // ✅ 회원 데이터 가져오기
    fetch(`/api/admin/members/${memberId}`)
        .then(response => response.json())
        .then(member => {
            console.log("✅ 회원 데이터:", member);

            if (!member || !member.id) {
                alert("회원 정보를 불러올 수 없습니다.");
                return;
            }

            let isActive = member.isActive ?? member.active;
            let role = member.role ?? "USER";

            // ✅ 상세 정보 채우기
            function setText(id, value) {
                const element = document.getElementById(id);
                if (element) element.textContent = value;
            }

            setText("member-id", member.id);
            setText("member-email", member.email);
            setText("member-username", member.username);
            setText("member-phone", member.phoneNumber || "-");
            setText("member-dob", member.dateOfBirth ? new Date(member.dateOfBirth).toLocaleDateString() : "-");
            setText("member-created", member.createdDate ? new Date(member.createdDate).toLocaleDateString() : "-");

            const statusElement = document.getElementById("member-status");
            if (statusElement) {
                statusElement.textContent = isActive ? "✅ 활성" : "❌ 비활성";
            }

            // ✅ 수정 모달에 값 채우기
            function setValue(id, value) {
                const element = document.getElementById(id);
                if (element) element.value = value;
            }

            setValue("edit-id", member.id);
            setValue("edit-email", member.email);
            setValue("edit-username", member.username);
            setValue("edit-phone", member.phoneNumber || "");
            setValue("edit-dob", member.dateOfBirth || "");
            setValue("edit-status", isActive.toString());
            setValue("edit-role", role);

            // ✅ "수정" 버튼 클릭 시 모달 표시
            if (editMemberBtn) {
                editMemberBtn.addEventListener("click", function () {
                    console.log("🛠 수정 버튼 클릭됨");
                    editMemberModal.show(); // ✅ Bootstrap 모달 표시
                });
            }

            // ✅ "저장" 버튼 클릭 시 데이터 업데이트
            if (saveMemberBtn) {
                saveMemberBtn.addEventListener("click", function () {
                    const updatedData = {
                        id: document.getElementById("edit-id").value,
                        email: document.getElementById("edit-email").value,
                        username: document.getElementById("edit-username").value,
                        phoneNumber: document.getElementById("edit-phone").value,
                        dateOfBirth: document.getElementById("edit-dob").value,
                        isActive: document.getElementById("edit-status").value === "true",
                        role: document.getElementById("edit-role").value
                    };

                    console.log("🔄 수정 요청 데이터:", updatedData);

                    fetch(`/api/admin/members/${memberId}`, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(updatedData)
                    })
                        .then(response => {
                            if (!response.ok) throw new Error("회원 정보 수정 실패");
                            return response.text();
                        })
                        .then(() => {
                            alert("회원 정보가 수정되었습니다.");
                            location.reload();
                        })
                        .catch(error => console.error("🚨 회원 정보 수정 실패:", error));
                });
            }

            // ✅ "닫기" 버튼 클릭 시 모달 닫기
            if (closeModalBtn) {
                closeModalBtn.addEventListener("click", function () {
                    editMemberModal.hide(); // ✅ Bootstrap 모달 닫기
                });
            }
        })
        .catch(error => console.error("🚨 회원 상세 정보 불러오기 실패:", error));
});