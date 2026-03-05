/**
 * Product Utilities - Format, escape, and validation functions
 */
const ProductUtils = (function() {
    'use strict';

    const MAX_STOCK = 1000000;
    const MAX_PRICE = 999999999999;

    function formatCurrency(value) {
        if (!value && value !== 0) return '—';
        return new Intl.NumberFormat('vi-VN').format(value) + ' ₫';
    }

    function formatDate(value) {
        if (!value) return '—';
        return new Intl.DateTimeFormat('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        }).format(new Date(value));
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function limitDigits(input, maxDigits) {
        if (!input) return;
        input.addEventListener('input', function(e) {
            let value = this.value.replace(/[^0-9]/g, '');
            if (value.length > maxDigits) {
                value = value.slice(0, maxDigits);
            }
            this.value = value;
        });
    }

    function validateNumericFields(ctx) {
        const stockValue = ctx.fields.stock?.value;
        const priceValue = ctx.fields.price?.value;

        if (stockValue) {
            const stock = parseInt(stockValue, 10);
            if (isNaN(stock) || stock < 0 || stock > MAX_STOCK) {
                alert(`Tồn kho phải là số từ 0 đến ${MAX_STOCK.toLocaleString('vi-VN')}`);
                ctx.fields.stock.focus();
                return false;
            }
        }

        if (priceValue) {
            const price = parseFloat(priceValue);
            if (isNaN(price) || price < 0 || price > MAX_PRICE) {
                alert(`Giá phải là số từ 0 đến ${MAX_PRICE.toLocaleString('vi-VN')} ₫`);
                ctx.fields.price.focus();
                return false;
            }
        }

        return true;
    }

    return {
        formatCurrency: formatCurrency,
        formatDate: formatDate,
        escapeHtml: escapeHtml,
        limitDigits: limitDigits,
        validateNumericFields: validateNumericFields,
        MAX_STOCK: MAX_STOCK,
        MAX_PRICE: MAX_PRICE
    };
})();
