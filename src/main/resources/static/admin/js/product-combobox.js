const ProductCombobox = (function () {
    'use strict';

    function initCombobox(inputEl, hiddenEl, dropdownEl, data, placeholder) {
        let selectedItem = null;
        let isOpen = false;

        function filterData(query) {
            if (!query || query.trim() === '') {
                return data;
            }
            const lowerQuery = query.toLowerCase();
            return data.filter(item =>
                item.name.toLowerCase().includes(lowerQuery)
            );
        }

        function renderDropdown(items) {
            dropdownEl.innerHTML = '';
            if (items.length === 0) {
                dropdownEl.innerHTML = '<li class="combobox-item disabled">Không tìm thấy</li>';
                return;
            }
            items.forEach(item => {
                const li = document.createElement('li');
                li.className = 'combobox-item';
                li.textContent = item.name;
                li.dataset.id = item.id;
                li.dataset.name = item.name;
                li.addEventListener('click', () => {
                    selectItem(item);
                });
                dropdownEl.appendChild(li);
            });
        }

        function selectItem(item) {
            selectedItem = item;
            inputEl.value = item.name || '';
            hiddenEl.value = item.id || '';
            closeDropdown();
        }

        function openDropdown() {
            isOpen = true;
            dropdownEl.style.display = 'block';
            renderDropdown(filterData(inputEl.value));
        }

        function closeDropdown() {
            isOpen = false;
            dropdownEl.style.display = 'none';
        }

        inputEl.addEventListener('focus', () => {
            openDropdown();
        });

        inputEl.addEventListener('input', (e) => {
            if (isOpen) {
                renderDropdown(filterData(e.target.value));
            } else {
                openDropdown();
            }
        });

        inputEl.addEventListener('blur', (e) => {
            setTimeout(() => {
                if (!dropdownEl.matches(':hover') && !inputEl.matches(':focus')) {
                    closeDropdown();
                    if (!selectedItem || inputEl.value !== selectedItem.name) {
                        if (selectedItem) {
                            inputEl.value = selectedItem.name;
                        } else {
                            inputEl.value = '';
                            hiddenEl.value = '';
                        }
                    }
                }
            }, 200);
        });

        document.addEventListener('click', (e) => {
            if (!inputEl.contains(e.target) && !dropdownEl.contains(e.target)) {
                closeDropdown();
            }
        });

        return {
            setValue: function (id, name) {
                selectedItem = { id: id, name: name };
                inputEl.value = name || '';
                hiddenEl.value = id || '';
            },
            getValue: function () {
                return hiddenEl.value;
            },
            clear: function () {
                selectedItem = null;
                inputEl.value = '';
                hiddenEl.value = '';
            }
        };
    }

    function initComboboxes(ctx, brandsData) {
        if (!ctx.fields) return { brandCombo: null };
        const brandCombo = initCombobox(
            ctx.fields.brandInput,
            ctx.fields.brandId,
            ctx.fields.brandDropdown,
            brandsData,
            'Chọn hãng'
        );
        return { brandCombo };
    }

    return {
        initCombobox: initCombobox,
        initComboboxes: initComboboxes
    };
})();
