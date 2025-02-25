document.getElementById("copyCustomerInfoBtn").addEventListener("click", function () {
    const street = document.getElementById("street");
    const city = document.getElementById("city");
    const zipcode = document.getElementById("zipcode");

    const customerStreet = document.getElementById("customerStreet");
    const customerCity = document.getElementById("customerCity");
    const customerZipcode = document.getElementById("customerZipcode");

    if (street && city && zipcode && customerStreet && customerCity && customerZipcode) {
        street.value = customerStreet.value;
        city.value = customerCity.value;
        zipcode.value = customerZipcode.value;
    } else {
        console.error("🚨 '회원 정보와 동일' 버튼 클릭 시 필요한 요소를 찾을 수 없음!");
    }
});