document.getElementById("dateOfBirth").addEventListener("change", function() {
    var date = new Date(this.value);
    var formattedDate = date.getFullYear() + '-' + ('0' + (date.getMonth() + 1)).slice(-2) + '-' + ('0' + date.getDate()).slice(-2);
    this.value = formattedDate;
});