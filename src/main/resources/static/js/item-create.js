document.addEventListener('DOMContentLoaded', function () {
    const categorySelect = document.getElementById('category');
    const optionsContainer = document.getElementById('optionsContainer');

    const categoryOptionsMap = {
        TOP: ['S', 'M', 'L', 'XL'],
        BOTTOM: ['26', '28', '30', '32', '34'],
        OUTER: ['S', 'M', 'L', 'XL'],
        SHOES: ['220', '230', '240', '250', '260', '270', '280', '290', '300'],
        BAG: ['OS'],
        ACCESSORY: ['OS'],
        ETC: ['OS']
    };

    categorySelect.addEventListener('change', function () {
        const selected = categorySelect.value;
        const sizes = categoryOptionsMap[selected] || [];

        optionsContainer.innerHTML = '';

        if (sizes.length > 0) {
            sizes.forEach((size, index) => {
                const group = document.createElement('div');
                group.classList.add('form-row', 'align-items-center', 'mb-2');

                group.innerHTML = `
                    <div class="col-auto">
                        <label>옵션 ${index + 1}</label>
                        <input type="text" class="form-control" name="options[${index}].sizeLabel" value="${size}" readonly>
                    </div>
                    <div class="col-auto">
                        <label>재고 수량</label>
                        <input type="number" class="form-control" name="options[${index}].stockQuantity" min="0" value="0" required>
                    </div>
                `;

                optionsContainer.appendChild(group);
            });
        }
    });
});