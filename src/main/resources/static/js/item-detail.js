document.addEventListener('DOMContentLoaded', function () {
    console.log("item-detail.js loaded");

    const optionSelect = document.getElementById('itemOption');
    const cartOptionId = document.getElementById('cartItemOptionId');
    const orderOptionId = document.getElementById('orderItemOptionId');

    const quantityInput = document.getElementById('quantity');
    const cartQuantity = document.getElementById('cartQuantity');
    const orderQuantity = document.getElementById('orderQuantity');

    optionSelect.addEventListener('change', function () {
        cartOptionId.value = this.value;
        orderOptionId.value = this.value;
    });

    quantityInput.addEventListener('change', function () {
        cartQuantity.value = this.value;
        orderQuantity.value = this.value;
    });
});

function changeQuantity(amount) {
    const quantityInput = document.getElementById('quantity');
    let current = parseInt(quantityInput.value, 10);
    const newValue = current + amount;
    if (newValue >= 1) {
        quantityInput.value = newValue;
        document.getElementById('cartQuantity').value = newValue;
        document.getElementById('orderQuantity').value = newValue;
    }
}