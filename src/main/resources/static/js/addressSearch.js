Kakao.init('fcb06fd71e67da6f5f16237578814762');

function openAddressSearch() {
    new daum.Postcode({
        oncomplete: function(data) {
            var roadAddr = data.roadAddress; // 도로명 주소
            var jibunAddr = data.jibunAddress; // 지번 주소
            document.getElementById('city').value = data.sido;
            document.getElementById('street').value = roadAddr || jibunAddr;
            document.getElementById('zipcode').value = data.zonecode;
        }
    }).open();
}