document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("copyCustomerInfoBtn").addEventListener("click", function () {
        document.getElementById("street").value = document.getElementById("customerStreet").value;
        document.getElementById("city").value = document.getElementById("customerCity").value;
        document.getElementById("zipcode").value = document.getElementById("customerZipcode").value;
         document.getElementById('additionalInfo').value = document.getElementById('customerAdditionalInfo').value;
    });
});
