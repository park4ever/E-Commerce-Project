document.addEventListener("DOMContentLoaded", function () {
    const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute("content"); // ✅ CSRF 토큰 가져오기

    document.querySelectorAll(".quantity-buttons").forEach(function (cartItem) {
        const itemId = cartItem.querySelector(".cart-item-id").value;
        const quantityInput = cartItem.querySelector(".cart-item-quantity");
        const decreaseBtn = cartItem.querySelector(".decrease-btn");
        const increaseBtn = cartItem.querySelector(".increase-btn");

        // AJAX 요청을 보내는 함수
        function updateCartQuantity(newQuantity) {
            if (newQuantity < 1) return; // 최소 1 이상만 가능

            fetch("/cart/update", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                    "X-CSRF-TOKEN": csrfToken
                },
                body: JSON.stringify({
                    itemId: parseInt(itemId, 10),
                    quantity: parseInt(newQuantity, 10)
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    quantityInput.value = newQuantity;
                    document.querySelector(".cart-total-price").innerText = data.cartTotal + " 원";

                    // ✅ 개별 상품의 합계 업데이트 (itemPrice * quantity)
                    const cartItemRow = quantityInput.closest("tr"); // 현재 상품이 있는 행 찾기
                    const itemPrice = parseInt(cartItemRow.querySelector("td:nth-child(3)").innerText, 10); // 상품 가격 가져오기
                    const totalElement = cartItemRow.querySelector(".cart-item-total"); // 개별 상품 총 가격 요소 찾기
                    totalElement.innerText = (itemPrice * newQuantity) + " 원"; // ✅ 개별 상품 합계 업데이트
                } else {
                    alert("수량 변경에 실패했습니다.");
                }
            })
            .catch(error => console.error("Error:", error));
        }

        // `+` 버튼 클릭 시 수량 증가
        increaseBtn.addEventListener("click", function () {
            const newQuantity = parseInt(quantityInput.value) + 1;
            updateCartQuantity(newQuantity);
        });

        // `-` 버튼 클릭 시 수량 감소
        decreaseBtn.addEventListener("click", function () {
            const newQuantity = parseInt(quantityInput.value) - 1;
            updateCartQuantity(newQuantity);
        });

        // 사용자가 직접 입력할 경우 반영
        quantityInput.addEventListener("change", function () {
            const newQuantity = parseInt(quantityInput.value, 10);
            if (isNaN(newQuantity) || newQuantity < 1) {
                quantityInput.value = 1; // ❌ 비정상적인 값은 최소 1로 자동 변경
                return;
            }
            updateCartQuantity(newQuantity);
        });
    });
});