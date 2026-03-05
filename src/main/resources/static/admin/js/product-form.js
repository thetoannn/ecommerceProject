const ProductForm = (function () {
    'use strict';

    let config = {};
    let createCtx, editCtx, detailFields;
    let createCombos, editCombos;
    let imageViewModal, imageViewModalImg;
    let pendingEditData = null;

    function init(options) {
        config = options;

        const baseUrl = config.baseUrl || '/admin/products';
        const brandsData = config.brandsData || [];
        const attributesData = config.attributesData || [];
        const productModalState = config.modalState || {};

        const detailModalEl = $('#productDetailModal');
        const createModalEl = $('#productCreateModal');
        const editModalEl = $('#productEditModal');

        const createFormEl = document.getElementById('productCreateForm');
        const createSubmitBtn = document.getElementById('productCreateFormSubmit');
        const createFields = createFormEl ? {
            name: createFormEl.querySelector('input[name="name"]'),
            price: createFormEl.querySelector('input[name="price"]'),
            stock: createFormEl.querySelector('input[name="stock"]'),
            images: createFormEl.querySelector('input[name="images"]'),
            description: createFormEl.querySelector('textarea[name="description"]'),
            brandId: document.getElementById('createProductBrand'),
            brandInput: document.getElementById('createProductBrandInput'),
            brandDropdown: document.getElementById('createProductBrandDropdown')
        } : null;

        const editFormEl = document.getElementById('productEditForm');
        const editSubmitBtn = document.getElementById('productEditFormSubmit');
        const editFields = editFormEl ? {
            id: editFormEl.querySelector('input[name="id"]'),
            name: editFormEl.querySelector('input[name="name"]'),
            price: editFormEl.querySelector('input[name="price"]'),
            stock: editFormEl.querySelector('input[name="stock"]'),
            images: editFormEl.querySelector('input[name="images"]'),
            description: editFormEl.querySelector('textarea[name="description"]'),
            brandId: document.getElementById('editProductBrand'),
            brandInput: document.getElementById('editProductBrandInput'),
            brandDropdown: document.getElementById('editProductBrandDropdown')
        } : null;

        imageViewModal = $('#imageViewModal');
        imageViewModalImg = document.getElementById('imageViewModalImg');

        createCtx = {
            modalEl: createModalEl,
            formEl: createFormEl,
            submitBtn: createSubmitBtn,
            fields: createFields,
            attrContainer: document.getElementById('createProductAttributesContainer'),
            imageErrorEl: document.getElementById('createImageError'),
            imagePreviewContainer: document.getElementById('createImagePreviewContainer'),
            imagePreviewList: document.getElementById('createImagePreviewList'),
            imageCountEl: document.getElementById('createImageCount'),
            existingImagesContainer: null,
            existingImagesList: null,
            isEdit: false
        };

        editCtx = {
            modalEl: editModalEl,
            formEl: editFormEl,
            submitBtn: editSubmitBtn,
            fields: editFields,
            attrContainer: document.getElementById('editProductAttributesContainer'),
            imageErrorEl: document.getElementById('editImageError'),
            imagePreviewContainer: document.getElementById('editImagePreviewContainer'),
            imagePreviewList: document.getElementById('editImagePreviewList'),
            imageCountEl: document.getElementById('editImageCount'),
            existingImagesContainer: document.getElementById('editExistingImagesContainer'),
            existingImagesList: document.getElementById('editExistingImagesList'),
            isEdit: true
        };

        detailFields = {
            id: document.getElementById('detailProductId'),
            name: document.getElementById('detailProductName'),
            price: document.getElementById('detailProductPrice'),
            stock: document.getElementById('detailProductStock'),
            brand: document.getElementById('detailProductBrand'),
            description: document.getElementById('detailProductDescription'),
            image: document.getElementById('detailProductImage'),
            created: document.getElementById('detailProductCreated'),
            updated: document.getElementById('detailProductUpdated'),
            attributes: document.getElementById('detailProductAttributesList')
        };

        // Apply digit limit to stock fields
        ProductUtils.limitDigits(createFields?.stock, 7);
        ProductUtils.limitDigits(editFields?.stock, 7);

        // Image change listeners
        if (createFields?.images) {
            createFields.images.addEventListener('change', function (e) {
                showImagePreview(createCtx, e.target.files);
            });
        }
        if (editFields?.images) {
            editFields.images.addEventListener('change', function (e) {
                showImagePreview(editCtx, e.target.files);
            });
        }

        // Initialize comboboxes
        createCombos = ProductCombobox.initComboboxes(createCtx, brandsData);
        editCombos = ProductCombobox.initComboboxes(editCtx, brandsData);

        // Attach submit handlers
        attachSubmitHandler(createCtx, baseUrl);
        attachSubmitHandler(editCtx, baseUrl);

        // View buttons - using event delegation
        document.addEventListener('click', (e) => {
            const viewBtn = e.target.closest('.btn-product-view');
            if (viewBtn) {
                console.log('View button clicked, ID:', viewBtn.getAttribute('data-id'));
                const id = viewBtn.getAttribute('data-id');
                fetchProduct(baseUrl, id, data => {
                    console.log('Product data fetched:', data);
                    populateDetail(data, attributesData);
                    console.log('Attempting to show detail modal...');
                    detailModalEl.modal('show');
                });
            }
        });

        // Edit buttons - using event delegation
        document.addEventListener('click', (e) => {
            const editBtn = e.target.closest('.btn-product-edit');
            if (editBtn) {
                console.log('Edit button clicked, ID:', editBtn.getAttribute('data-id'));
                const id = editBtn.getAttribute('data-id');
                fetchProduct(baseUrl, id, data => {
                    console.log('Product data fetched for edit:', data);
                    pendingEditData = data;
                    console.log('Attempting to show edit modal...');
                    editModalEl.modal('show');
                });
            }
        });

        // Create button
        const createBtn = document.querySelector('[data-action="open-product-modal"]');
        if (createBtn) {
            createBtn.addEventListener('click', () => {
                pendingEditData = null;
                resetFormFields(createCtx, attributesData);
                populateForm(createCtx, createCombos, {}, brandsData, attributesData);
                createModalEl.modal('show');
            });
        }

        // Auto-open modal if needed
        if (productModalState.shouldOpen) {
            const hasSuccessMessage = document.querySelector('.alert-success');
            if (!hasSuccessMessage) {
                if (productModalState.mode === 'edit') {
                    editModalEl.modal('show');
                } else {
                    createModalEl.modal('show');
                }
            }
            productModalState.shouldOpen = false;
        }

        // Modal events
        editModalEl.on('shown.bs.modal', () => {
            if (pendingEditData) {
                populateForm(editCtx, editCombos, pendingEditData, brandsData, attributesData);
                pendingEditData = null;
            }
        });

        createModalEl.on('hidden.bs.modal', () => {
            if (!productModalState.shouldOpen) {
                resetFormFields(createCtx, attributesData);
            }
        });

        editModalEl.on('hidden.bs.modal', () => {
            if (!productModalState.shouldOpen) {
                resetFormFields(editCtx, attributesData);
                hideExistingImages(editCtx);
            }
        });

        // Detail image click
        if (detailFields.image) {
            detailFields.image.style.cursor = 'pointer';
            detailFields.image.addEventListener('click', function () {
                if (this.src && !this.src.includes('placeholder')) {
                    imageViewModalImg.src = this.src;
                    imageViewModal.modal('show');
                }
            });
        }

        // Refresh images after update
        handleImageRefresh(baseUrl);

        // Export deleteProductImage to window
        window.deleteProductImage = function (imageId, buttonEl) {
            deleteProductImage(imageId, buttonEl, editCtx);
        };
    }

    function resetFormFields(ctx, attributesData) {
        if (!ctx.formEl) return;
        ctx.formEl.reset();
        if (ctx.fields.id) ctx.fields.id.value = '';
        if (ctx.existingImagesContainer && ctx.existingImagesList) {
            ctx.existingImagesContainer.style.display = 'none';
            ctx.existingImagesList.innerHTML = '';
        }
        if (ctx.imagePreviewContainer && ctx.imagePreviewList) {
            ctx.imagePreviewContainer.style.display = 'none';
            ctx.imagePreviewList.innerHTML = '';
        }
        renderAttributesUI(ctx, {}, attributesData);
    }

    function renderAttributesUI(ctx, selectedAttributes, attributesData) {
        if (!ctx.attrContainer) return;
        ctx.attrContainer.innerHTML = '';

        if (!attributesData || attributesData.length === 0) {
            ctx.attrContainer.innerHTML = '<p class="text-muted mb-0"><small>Chưa có thông số kĩ thuật nào. <a href="/admin/attributes" target="_blank">Thêm thông số kĩ thuật</a></small></p>';
            return;
        }

        attributesData.forEach((attr, index) => {
            const attrId = typeof attr.id === 'string' ? parseInt(attr.id, 10) : attr.id;
            const selectedValueId = selectedAttributes[attrId] || '';
            const attrRow = document.createElement('div');
            attrRow.className = 'form-row mb-2';
            attrRow.innerHTML = `
                <div class="col-md-4">
                    <label class="mb-0 mt-2"><strong>${ProductUtils.escapeHtml(attr.name)}</strong></label>
                    <input type="hidden" name="attributes[${index}].attributeId" value="${attrId}">
                </div>
                <div class="col-md-8">
                    <select class="form-control form-control-sm" name="attributes[${index}].valueId" data-attr-id="${attrId}">
                        <option value="">-- Không chọn --</option>
                        ${attr.values.map(v => {
                const valueId = typeof v.id === 'string' ? parseInt(v.id, 10) : v.id;
                const isSelected = valueId == selectedValueId || String(valueId) === String(selectedValueId);
                return `<option value="${valueId}" ${isSelected ? 'selected' : ''}>${ProductUtils.escapeHtml(v.value)}</option>`;
            }).join('')}
                    </select>
                    ${attr.values.length === 0 ? '<small class="form-text text-muted">Chưa có giá trị. <a href="/admin/attribute-values?attributeId=' + attrId + '" target="_blank">Thêm giá trị</a></small>' : ''}
                </div>
            `;
            ctx.attrContainer.appendChild(attrRow);
        });
    }

    function hideImagePreview(ctx) {
        if (!ctx.imagePreviewContainer || !ctx.imagePreviewList) return;
        ctx.imagePreviewContainer.style.display = 'none';
        ctx.imagePreviewList.innerHTML = '';
        if (ctx.imageCountEl) ctx.imageCountEl.textContent = '0';
    }

    function validateImages(ctx, files, requireMin) {
        if (!ctx.imageErrorEl) return true;
        const imageError = ctx.imageErrorEl;
        const validFiles = Array.from(files || []).filter(file => file.type.startsWith('image/'));

        imageError.style.display = 'none';
        imageError.textContent = '';

        if (requireMin && validFiles.length === 0) {
            imageError.textContent = 'Vui lòng chọn ít nhất 1 ảnh';
            imageError.style.display = 'block';
            return false;
        }
        if (validFiles.length > 6) {
            imageError.textContent = 'Chỉ được chọn tối đa 6 ảnh';
            imageError.style.display = 'block';
            return false;
        }
        return true;
    }

    function showImagePreview(ctx, files) {
        if (!ctx.imagePreviewContainer || !ctx.imagePreviewList || !ctx.fields?.images) return;
        if (!files || files.length === 0) {
            hideImagePreview(ctx);
            return;
        }

        if (!validateImages(ctx, files, ctx.isEdit ? false : true)) {
            ctx.fields.images.value = '';
            return;
        }

        const validFiles = Array.from(files).filter(file => file.type.startsWith('image/'));
        ctx.imagePreviewContainer.style.display = 'block';
        ctx.imagePreviewList.innerHTML = '';
        if (ctx.imageCountEl) ctx.imageCountEl.textContent = validFiles.length;

        validFiles.forEach((file, index) => {
            const reader = new FileReader();
            reader.onload = function (e) {
                const imgDiv = document.createElement('div');
                imgDiv.className = 'position-relative product-image-thumb';
                imgDiv.style.cssText = 'width: 120px; height: 120px; cursor: pointer;';
                imgDiv.innerHTML = `
                    <img src="${e.target.result}" alt="Preview ${index + 1}"
                         class="rounded border"
                         style="width: 100%; height: 100%; object-fit: cover;">
                    ${index === 0 ? '<span class="badge badge-primary position-absolute" style="bottom: 2px; left: 2px; font-size: 0.7rem;">Ảnh chính</span>' : ''}
                `;
                imgDiv.addEventListener('click', () => {
                    imageViewModalImg.src = e.target.result;
                    imageViewModal.modal('show');
                });
                ctx.imagePreviewList.appendChild(imgDiv);
            };
            reader.readAsDataURL(file);
        });
    }

    function attachSubmitHandler(ctx, baseUrl) {
        if (!ctx.formEl) return;
        let isSubmitting = false;
        ctx.formEl.addEventListener('submit', function (e) {
            if (isSubmitting) {
                e.preventDefault();
                e.stopPropagation();
                return false;
            }

            if (!ProductUtils.validateNumericFields(ctx)) {
                e.preventDefault();
                e.stopPropagation();
                return false;
            }

            if (ctx.isEdit) {
                const productId = ctx.fields.id?.value;
                if (!productId) {
                    e.preventDefault();
                    e.stopPropagation();
                    alert('Không tìm thấy ID sản phẩm');
                    return false;
                }
                ctx.formEl.action = baseUrl + '/' + productId + '/update';
                const hasExistingImages = ctx.existingImagesList && ctx.existingImagesList.children.length > 0;
                const hasNewImages = ctx.fields.images && ctx.fields.images.files && ctx.fields.images.files.length > 0;

                if (!hasExistingImages && !hasNewImages) {
                    e.preventDefault();
                    e.stopPropagation();
                    if (ctx.imageErrorEl) {
                        ctx.imageErrorEl.textContent = 'Sản phẩm phải có ít nhất 1 ảnh. Vui lòng thêm ảnh mới hoặc không xóa tất cả ảnh hiện có.';
                        ctx.imageErrorEl.style.display = 'block';
                    }
                    return false;
                }

                if (hasNewImages) {
                    const existingCount = hasExistingImages ? ctx.existingImagesList.children.length : 0;
                    const newCount = Array.from(ctx.fields.images.files).filter(f => f.type.startsWith('image/')).length;
                    const totalCount = existingCount + newCount;
                    if (totalCount > 6) {
                        e.preventDefault();
                        e.stopPropagation();
                        if (ctx.imageErrorEl) {
                            ctx.imageErrorEl.textContent = `Tổng số ảnh không được vượt quá 6 (hiện có ${existingCount} ảnh, đang thêm ${newCount} ảnh)`;
                            ctx.imageErrorEl.style.display = 'block';
                        }
                        ctx.fields.images.focus();
                        return false;
                    }
                    if (!validateImages(ctx, ctx.fields.images.files, false)) {
                        e.preventDefault();
                        e.stopPropagation();
                        ctx.fields.images.focus();
                        return false;
                    }
                }
            } else {
                const hasNewImages = ctx.fields.images && ctx.fields.images.files && ctx.fields.images.files.length > 0;
                if (!hasNewImages || !validateImages(ctx, ctx.fields.images.files, true)) {
                    e.preventDefault();
                    e.stopPropagation();
                    ctx.fields.images.focus();
                    return false;
                }
                ctx.formEl.action = baseUrl;
            }

            isSubmitting = true;
            if (ctx.submitBtn) {
                ctx.submitBtn.disabled = true;
                const originalText = ctx.submitBtn.textContent;
                ctx.submitBtn.textContent = 'Đang lưu...';
                setTimeout(() => {
                    isSubmitting = false;
                    if (ctx.submitBtn) {
                        ctx.submitBtn.disabled = false;
                        ctx.submitBtn.textContent = originalText;
                    }
                }, 5000);
            }
            return true;
        }, { once: false });
    }

    function hideExistingImages(ctx) {
        if (ctx.existingImagesContainer && ctx.existingImagesList) {
            ctx.existingImagesContainer.style.display = 'none';
            ctx.existingImagesList.innerHTML = '';
        }
    }

    function displayExistingImages(ctx, images) {
        if (!ctx.existingImagesContainer || !ctx.existingImagesList) return;
        if (!images || images.length === 0) {
            hideExistingImages(ctx);
            return;
        }
        ctx.existingImagesContainer.style.display = 'block';
        ctx.existingImagesList.innerHTML = '';
        images.forEach(img => {
            const imgDiv = document.createElement('div');
            imgDiv.className = 'position-relative product-image-thumb';
            imgDiv.style.cssText = 'width: 120px; height: 120px; cursor: pointer;';
            imgDiv.innerHTML = `
                <img src="${img.imagePath}" alt="Product image"
                     class="rounded border"
                     style="width: 100%; height: 100%; object-fit: cover;">
                <button type="button"
                        class="btn btn-sm btn-danger position-absolute"
                        style="top: 2px; right: 2px; padding: 2px 6px; z-index: 10;"
                        data-image-id="${img.id}"
                        onclick="event.stopPropagation(); deleteProductImage(${img.id}, this)">
                    <i class="ti-close"></i>
                </button>
                ${img.isPrimary ? '<span class="badge badge-primary position-absolute" style="bottom: 2px; left: 2px; font-size: 0.7rem;">Chính</span>' : ''}
            `;
            imgDiv.addEventListener('click', function (e) {
                if (e.target.tagName !== 'BUTTON' && !e.target.closest('button')) {
                    imageViewModalImg.src = img.imagePath;
                    imageViewModal.modal('show');
                }
            });
            ctx.existingImagesList.appendChild(imgDiv);
        });
    }

    function deleteProductImage(imageId, buttonEl, ctx) {
        if (!confirm('Bạn có chắc muốn xóa ảnh này?')) {
            return;
        }
        fetch('/admin/products/images/' + imageId + '/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(resp => {
                if (resp.ok) {
                    return resp.text().then(data => {
                        buttonEl.closest('.position-relative').remove();
                        if (ctx.existingImagesList.children.length === 0) {
                            hideExistingImages(ctx);
                        }
                    });
                } else {
                    return resp.text().then(data => {
                        alert('Lỗi khi xóa ảnh: ' + data);
                    });
                }
            })
            .catch(err => {
                alert('Lỗi khi xóa ảnh: ' + err.message);
            });
    }

    function populateForm(ctx, combos, data, brandsData, attributesData) {
        if (!ctx.fields) return;
        if (ctx.fields.id) ctx.fields.id.value = data.id || '';
        ctx.fields.name.value = data.name || '';
        ctx.fields.price.value = data.price != null ? data.price : '';
        ctx.fields.stock.value = data.stock != null ? data.stock : '';
        ctx.fields.description.value = data.description || '';

        const brand = brandsData.find(b => b.id == data.brandId);

        if (combos.brandCombo && brand) {
            combos.brandCombo.setValue(brand.id, brand.name);
        }

        if (ctx.isEdit) {
            if (data.images && data.images.length > 0) {
                displayExistingImages(ctx, data.images);
            } else {
                hideExistingImages(ctx);
            }
        }

        const selectedAttributes = {};
        if (data.attributes) {
            Object.keys(data.attributes).forEach(attrId => {
                const attrIdNum = parseInt(attrId, 10);
                const attrData = data.attributes[attrId];
                if (attrData && attrData.attributeValueId != null) {
                    selectedAttributes[attrIdNum] = attrData.attributeValueId;
                }
            });
        }
        renderAttributesUI(ctx, selectedAttributes, attributesData);
    }

    function populateDetail(data, attributesData) {
        detailFields.id.textContent = data.id || '—';
        detailFields.name.textContent = data.name || '—';
        detailFields.price.textContent = ProductUtils.formatCurrency(data.price);
        detailFields.stock.textContent = data.stock != null ? data.stock : '—';
        detailFields.brand.textContent = data.brandName || '—';
        detailFields.description.textContent = data.description || 'Chưa có mô tả';

        if (detailFields.attributes) {
            if (data.attributes && Object.keys(data.attributes).length > 0) {
                detailFields.attributes.innerHTML = '';
                Object.values(data.attributes).forEach(attr => {
                    const badge = document.createElement('span');
                    badge.className = 'badge badge-info mb-1';
                    badge.style.cssText = 'font-size: 0.875rem; padding: 0.5rem 0.75rem; white-space: nowrap;';
                    const attrName = ProductUtils.escapeHtml(attr.attributeName || '');
                    const attrValue = ProductUtils.escapeHtml(attr.attributeValueName || '');
                    badge.innerHTML = `<strong>${attrName}</strong>: ${attrValue}`;
                    detailFields.attributes.appendChild(badge);
                });
            } else {
                detailFields.attributes.innerHTML = '<span class="text-muted small">Chưa có thông số kĩ thuật</span>';
            }
        }

        const primaryImage = data.images && data.images.length > 0
            ? data.images.find(img => img.isPrimary) || data.images[0]
            : null;
        detailFields.image.src = primaryImage ? primaryImage.imagePath : 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAiIGhlaWdodD0iODAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjgwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjZGRkIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPk5vIEltYWdlPC90ZXh0Pjwvc3ZnPg==';

        const detailImagesContainer = document.getElementById('detailProductImagesContainer');
        const detailImagesList = document.getElementById('detailProductImagesList');
        if (data.images && data.images.length > 0) {
            detailImagesContainer.style.display = 'block';
            detailImagesList.innerHTML = '';
            data.images.forEach(img => {
                const imgDiv = document.createElement('div');
                imgDiv.className = 'position-relative product-image-thumb';
                imgDiv.style.cssText = 'width: 120px; height: 120px; cursor: pointer;';
                imgDiv.innerHTML = `
                    <img src="${img.imagePath}" alt="Product image"
                         class="rounded border"
                         style="width: 100%; height: 100%; object-fit: cover;">
                    ${img.isPrimary ? '<span class="badge badge-primary position-absolute" style="bottom: 2px; left: 2px; font-size: 0.7rem;">Chính</span>' : ''}
                `;
                imgDiv.addEventListener('click', () => {
                    imageViewModalImg.src = img.imagePath;
                    imageViewModal.modal('show');
                });
                detailImagesList.appendChild(imgDiv);
            });
        } else {
            detailImagesContainer.style.display = 'none';
        }

        detailFields.created.textContent = ProductUtils.formatDate(data.createdAt);
        detailFields.updated.textContent = ProductUtils.formatDate(data.updatedAt);
    }

    function fetchProduct(baseUrl, id, onSuccess) {
        fetch(baseUrl + '/' + id + '/json', {
            headers: { 'Accept': 'application/json' }
        })
            .then(resp => {
                if (!resp.ok) throw new Error('Không lấy được dữ liệu sản phẩm');
                return resp.json();
            })
            .then(onSuccess)
            .catch(err => {
                alert(err.message);
            });
    }

    function handleImageRefresh(baseUrl) {
        const refreshImagesFlag = document.getElementById('refreshImagesFlag');
        if (refreshImagesFlag) {
            const updatedProductId = refreshImagesFlag.getAttribute('data-product-id');
            if (updatedProductId && updatedProductId !== 'null' && updatedProductId !== '') {
                fetch(baseUrl + '/' + updatedProductId + '/json', {
                    headers: { 'Accept': 'application/json' }
                })
                    .then(resp => {
                        if (!resp.ok) {
                            console.error('Failed to fetch updated product data');
                            return null;
                        }
                        return resp.json();
                    })
                    .then(productData => {
                        if (productData) {
                            const primaryImage = productData.images && productData.images.length > 0
                                ? productData.images.find(img => img.isPrimary) || productData.images[0]
                                : null;
                            const newImagePath = primaryImage ? primaryImage.imagePath : '';

                            document.querySelectorAll('.product-thumb-img[data-product-id="' + updatedProductId + '"]').forEach(img => {
                                if (newImagePath) {
                                    const separator = newImagePath.includes('?') ? '&' : '?';
                                    img.src = newImagePath + separator + 'v=' + new Date().getTime();
                                }
                            });
                        }
                    })
                    .catch(err => {
                        console.error('Error refreshing product image:', err);
                        document.querySelectorAll('.product-thumb-img[data-product-id="' + updatedProductId + '"]').forEach(img => {
                            const currentSrc = img.src;
                            if (currentSrc && !currentSrc.includes('placeholder') && !currentSrc.startsWith('data:')) {
                                const separator = currentSrc.includes('?') ? '&' : '?';
                                img.src = currentSrc + separator + 'v=' + new Date().getTime();
                            }
                        });
                    });
            }
        }
    }

    return {
        init: init
    };
})();
