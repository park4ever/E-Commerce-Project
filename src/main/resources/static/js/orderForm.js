document.addEventListener("DOMContentLoaded", function () {
    const copyBtn = document.getElementById("copyCustomerInfoBtn");
    const openAddressSearchBtn = document.getElementById("openAddressSearchBtn");

    // ✅ 카카오 API 초기화
    if (typeof Kakao !== "undefined") {
        Kakao.init("fcb06fd71e67da6f5f16237578814762"); // ✅ 카카오 API 키 등록
    } else {
        console.error("🚨 Kakao API가 로드되지 않았습니다!");
    }

    // 🚀 "회원 정보와 동일" 버튼 이벤트 추가
    if (copyBtn) {
        copyBtn.addEventListener("click", function () {
            const street = document.getElementById("street");
            const city = document.getElementById("city");
            const zipcode = document.getElementById("zipcode");
            const additionalInfo = document.getElementById("additionalInfo");

            const customerStreet = document.getElementById("customerStreet");
            const customerCity = document.getElementById("customerCity");
            const customerZipcode = document.getElementById("customerZipcode");
            const customerAdditionalInfo = document.getElementById("customerAdditionalInfo");

            if (street && city && zipcode && additionalInfo && customerStreet && customerCity && customerZipcode && customerAdditionalInfo) {
                console.log(`✅ 회원 정보 복사: ${customerStreet.value}, ${customerCity.value}, ${customerZipcode.value}`);
                street.value = customerStreet.value;
                city.value = customerCity.value;
                zipcode.value = customerZipcode.value;
                additionalInfo.value = customerAdditionalInfo.value;
            } else {
                console.error("🚨 '회원 정보와 동일' 버튼 클릭 시 필요한 요소를 찾을 수 없음!");
            }
        });
    } else {
        console.error("🚨 copyCustomerInfoBtn 버튼을 찾을 수 없음!");
    }

    // 🚀 카카오 주소 검색 기능 추가
    function openAddressSearch() {
        new daum.Postcode({
            oncomplete: function(data) {
                var roadAddr = data.roadAddress;
                var jibunAddr = data.jibunAddress;
                var address = roadAddr || jibunAddr;

                // 배송지 정보 입력 필드에 값 설정
                document.getElementById('street').value = address;
                document.getElementById('zipcode').value = data.zonecode;
                document.getElementById('city').value = data.sido;
            }
        }).open();
    }

    // 🚀 "주소 검색" 버튼 이벤트 추가
    if (openAddressSearchBtn) {
        openAddressSearchBtn.addEventListener("click", openAddressSearch);
    } else {
        console.error("🚨 openAddressSearchBtn 버튼을 찾을 수 없음!");
    }

    // 🚀 수량 변경 시 총 가격 자동 업데이트 기능 추가
    const quantityInputs = document.querySelectorAll(".order-item-quantity");

    function updateTotalPrice(event) {
        const input = event.target;
        const orderItemRow = input.closest(".order-item");
        const orderPrice = parseFloat(orderItemRow.querySelector(".order-item-price").value); // 개별 상품 가격
        const totalElement = orderItemRow.querySelector(".order-item-total"); // 총 가격 필드

        const newQuantity = parseInt(input.value, 10);
        if (isNaN(newQuantity) || newQuantity < 1) {
            input.value = 1; // 최소 1개 이상 유지
            return;
        }

        totalElement.value = (orderPrice * newQuantity).toLocaleString() + " 원"; // 총 가격 업데이트
    }

    // 모든 수량 입력 필드에 이벤트 리스너 추가
    quantityInputs.forEach(input => {
        input.addEventListener("input", updateTotalPrice); // 키보드 입력 시
        input.addEventListener("change", updateTotalPrice); // 입력 완료 시
    });
});