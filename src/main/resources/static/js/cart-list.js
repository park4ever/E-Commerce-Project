document.addEventListener("DOMContentLoaded", function () {
    const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute("content");

    document.querySelectorAll(".quantity-buttons").forEach(function (cartItem) {
        const cartItemId = cartItem.querySelector(".cart-item-id").value;
        const quantityInput = cartItem.querySelector(".cart-item-quantity");
        const decreaseBtn = cartItem.querySelector(".decrease-btn");
        const increaseBtn = cartItem.querySelector(".increase-btn");

        function updateCartQuantity(newQuantity) {
            if (newQuantity < 1) return;

            fetch("/cart/update", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                    "X-CSRF-TOKEN": csrfToken
                },
                body: JSON.stringify({
                    cartItemId: parseInt(cartItemId, 10),
                    quantity: parseInt(newQuantity, 10)
                })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        quantityInput.value = newQuantity;
                        document.querySelector(".cart-total-price").innerText = data.cartTotal + " 원";

                        const cartItemRow = quantityInput.closest("tr");
                        const itemPrice = parseInt(cartItemRow.querySelector("td:nth-child(3)").innerText, 10);
                        const totalElement = cartItemRow.querySelector(".cart-item-total");
                        totalElement.innerText = (itemPrice * newQuantity) + " 원";
                    } else {
                        alert("수량 변경에 실패했습니다.");
                    }
                })
                .catch(error => {
                    console.error("에러 발생:", error);
                    alert("서버 오류로 수량 변경에 실패했습니다.");
                });
        }

        decreaseBtn.addEventListener("click", () => {
            const currentQuantity = parseInt(quantityInput.value, 10);
            updateCartQuantity(currentQuantity - 1);
        });

        increaseBtn.addEventListener("click", () => {
            const currentQuantity = parseInt(quantityInput.value, 10);
            updateCartQuantity(currentQuantity + 1);
        });
    });

    document.querySelectorAll(".delete-cart-item-btn").forEach(function (button) {
        button.addEventListener("click", function () {
            const cartItemId = button.getAttribute("data-cart-item-id");

            if (!confirm("이 항목을 장바구니에서 삭제하시겠습니까?")) return;

            fetch("/cart/remove", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "X-CSRF-TOKEN": csrfToken
                },
                body: new URLSearchParams({
                    cartItemId: cartItemId
                })
            })
            .then(() => {
                // 페이지 새로고침
                window.location.reload();
            })
            .catch(error => {
                console.error("삭제 중 오류 발생:", error);
                alert("장바구니 항목 삭제에 실패했습니다.");
            });
        });
    });
});