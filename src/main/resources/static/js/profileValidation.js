document.addEventListener("DOMContentLoaded", function() {
    var dateInput = document.getElementById("dateOfBirth");
    if (dateInput) {  // 요소가 존재할 때만 이벤트 리스너 추가
        dateInput.addEventListener("change", function() {
            var date = new Date(this.value);
            var formattedDate = date.getFullYear() + '-' +
                ('0' + (date.getMonth() + 1)).slice(-2) + '-' +
                ('0' + date.getDate()).slice(-2);
            this.value = formattedDate;
        });
    }
});
