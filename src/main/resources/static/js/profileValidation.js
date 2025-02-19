document.addEventListener("DOMContentLoaded", function() {
    var dateInput = document.getElementById("dateOfBirth");
    var newPassword = document.getElementById("newPassword");
    var confirmNewPassword = document.getElementById("confirmNewPassword");
    var form = document.querySelector("form");

    // ✅ 디버깅 로그 추가
    console.log("profileValidation.js 실행됨!");

    // 생년월일 포맷팅
    if (dateInput) {
        dateInput.addEventListener("change", function() {
            var date = new Date(this.value);
            var formattedDate = date.getFullYear() + '-' +
                ('0' + (date.getMonth() + 1)).slice(-2) + '-' +
                ('0' + date.getDate()).slice(-2);
            this.value = formattedDate;
        });
    }

    // 비밀번호 확인 검증 로직
    function validatePassword() {
        console.log("validatePassword() 실행됨."); // ✅ 디버깅 로그 추가
        if (newPassword.value !== "" || confirmNewPassword.value !== "") {
            console.log("비밀번호 입력 감지됨."); // ✅ 디버깅 로그
            if (newPassword.value !== confirmNewPassword.value) {
                console.log("비밀번호 불일치! alert 실행"); // ✅ 디버깅 로그
                alert("새 비밀번호가 일치하지 않습니다.");
                return false; // 🔥 폼 제출 차단
            }
        }
        return true;
    }

    // ✅ 폼 제출 이벤트 리스너가 실행되는지 확인
    if (form) {
        console.log("폼 요소 감지됨!"); // ✅ 디버깅 로그
        form.addEventListener("submit", function(event) {
            console.log("폼 제출 이벤트 발생!"); // ✅ 디버깅 로그
            if (!validatePassword()) {
                console.log("폼 제출 중단됨!"); // ✅ 디버깅 로그
                event.preventDefault(); // 🔥 폼 제출 차단
                return false;
            }
        });
    } else {
        console.log("폼 요소를 찾을 수 없음!"); // ❌ 폼이 없으면 오류 로그 출력
    }
});