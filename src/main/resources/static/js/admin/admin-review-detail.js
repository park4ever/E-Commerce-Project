const reviewId = window.location.pathname.split("/").pop();

window.addEventListener("DOMContentLoaded", () => {
    loadReviewDetail();

    document.getElementById("deleteBtn")?.addEventListener("click", deleteReview);
    document.getElementById("editReviewBtn")?.addEventListener("click", () => {
        const modal = new bootstrap.Modal(document.getElementById("editReviewModal"));
        modal.show();
    });
    document.getElementById("saveReviewBtn")?.addEventListener("click", saveReviewChanges);
    document.getElementById("deleteReplyBtn")?.addEventListener("click", deleteReply);
});

function loadReviewDetail() {
    fetch(`/api/admin/reviews/${reviewId}`)
        .then(response => {
            if (!response.ok) throw new Error("리뷰 정보를 불러오는 데 실패했습니다.");
            return response.json();
        })
        .then(review => {
            document.getElementById("review-id").textContent = review.id;
            document.getElementById("item-name").textContent = review.itemName;
            document.getElementById("member-name").textContent = review.memberName;
            document.getElementById("member-email").textContent = review.memberEmail;
            document.getElementById("rating").textContent = "⭐".repeat(review.rating);
            document.getElementById("content").textContent = review.content;

            const imageElement = document.getElementById("review-image");
            if (review.imageUrl && review.imageUrl.trim() !== "") {
                imageElement.src = review.imageUrl.startsWith("/") ? review.imageUrl : `/images/${review.imageUrl}`;
                imageElement.alt = "리뷰 이미지";
                imageElement.style.display = "block";
            } else {
                imageElement.src = "";
                imageElement.alt = "이미지 없음";
                imageElement.style.display = "none";
            }

            document.getElementById("is-visible").textContent = review.isVisible ? "공개" : "비공개";
            document.getElementById("admin-reply").textContent = review.adminReply ?? "-";
            document.getElementById("created-date").textContent = formatDate(review.createdDate);
            document.getElementById("last-modified-date").textContent = formatDate(review.lastModifiedDate);

            // 모달 초기화
            document.getElementById("edit-visible").value = String(review.isVisible);
            document.getElementById("edit-reply").value = review.adminReply || "";
        })
        .catch(err => {
            console.error(err);
            alert("리뷰 정보를 불러오는 데 실패했습니다.");
        });
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return isNaN(date) ? "-" : date.toLocaleString("ko-KR");
}

function deleteReview() {
    if (!confirm("정말 이 리뷰를 삭제하시겠습니까?")) return;

    fetch(`/api/admin/reviews/${reviewId}`, { method: "DELETE" })
        .then(response => {
            if (!response.ok) throw new Error();
            alert("리뷰가 삭제되었습니다.");
            window.location.href = "/admin/reviews";
        })
        .catch(() => alert("리뷰 삭제 실패"));
}

function saveReviewChanges() {
    const reply = document.getElementById("edit-reply").value.trim();
    const visibility = document.getElementById("edit-visible").value === "true";

    const promises = [];

    // 관리자 답변 저장
    if (reply !== "") {
        promises.push(fetch(`/api/admin/reviews/${reviewId}/reply`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(reply)
        }));
    }

    // 공개 여부 변경
    const current = document.getElementById("is-visible").textContent === "공개";
    if (current !== visibility) {
        promises.push(fetch(`/api/admin/reviews/${reviewId}/visibility?isVisible=${visibility}`, {
            method: "PUT"
        }));
    }

    if (promises.length === 0) {
        alert("변경된 내용이 없습니다.");
        return;
    }

    Promise.all(promises)
        .then(responses => {
            if (responses.every(r => r.ok)) {
                alert("리뷰가 성공적으로 수정되었습니다.");
                bootstrap.Modal.getInstance(document.getElementById("editReviewModal")).hide();
                loadReviewDetail();
            } else {
                throw new Error();
            }
        })
        .catch(() => alert("리뷰 수정 실패"));
}

function deleteReply() {
    if (!confirm("정말 답변을 삭제하시겠습니까?")) return;

    fetch(`/api/admin/reviews/${reviewId}/reply`, { method: "DELETE" })
        .then(response => {
            if (!response.ok) throw new Error();
            alert("답변이 삭제되었습니다.");
            bootstrap.Modal.getInstance(document.getElementById("editReviewModal")).hide();
            loadReviewDetail();
        })
        .catch(() => alert("답변 삭제 실패"));
}