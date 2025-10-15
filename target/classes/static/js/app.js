// app.js - Main application JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

function initializeApp() {
    // Auto-dismiss alerts after 5 seconds
    autoDismissAlerts();
    
    // Initialize tooltips
    initializeTooltips();
    
    // Initialize form validation
    initializeFormValidation();
    
    // Initialize image previews
    initializeImagePreviews();
    
    // Initialize quantity controls
    initializeQuantityControls();
}

// Auto-dismiss Bootstrap alerts
function autoDismissAlerts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert.classList.contains('show')) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        }, 5000);
    });
}

// Initialize Bootstrap tooltips
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// Form validation
function initializeFormValidation() {
    const forms = document.querySelectorAll('.needs-validation');
    
    forms.forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
}

// Image preview for product forms
function initializeImagePreviews() {
    const imageUrlInputs = document.querySelectorAll('input[type="url"][id*="image"]');
    
    imageUrlInputs.forEach(input => {
        input.addEventListener('input', function() {
            const previewContainer = this.parentElement.querySelector('.image-preview');
            const previewImg = this.parentElement.querySelector('.image-preview img');
            const url = this.value;
            
            if (url && isValidUrl(url)) {
                if (!previewContainer) {
                    createImagePreview(this);
                } else {
                    updateImagePreview(previewImg, url);
                }
            } else if (previewContainer) {
                previewContainer.style.display = 'none';
            }
        });
    });
}

function isValidUrl(string) {
    try {
        new URL(string);
        return true;
    } catch (_) {
        return false;
    }
}

function createImagePreview(inputElement) {
    const previewContainer = document.createElement('div');
    previewContainer.className = 'image-preview mt-2';
    
    const previewImg = document.createElement('img');
    previewImg.src = inputElement.value;
    previewImg.alt = 'Image preview';
    previewImg.className = 'img-thumbnail';
    previewImg.style.maxHeight = '200px';
    
    previewImg.onload = function() {
        previewContainer.style.display = 'block';
    };
    
    previewImg.onerror = function() {
        previewContainer.style.display = 'none';
    };
    
    previewContainer.appendChild(previewImg);
    inputElement.parentElement.appendChild(previewContainer);
}

function updateImagePreview(previewImg, url) {
    previewImg.src = url;
    previewImg.parentElement.style.display = 'block';
}

// Quantity controls for product pages
function initializeQuantityControls() {
    const quantityInputs = document.querySelectorAll('.quantity-input');
    
    quantityInputs.forEach(input => {
        const decrementBtn = input.parentElement.querySelector('.quantity-decrement');
        const incrementBtn = input.parentElement.querySelector('.quantity-increment');
        
        if (decrementBtn) {
            decrementBtn.addEventListener('click', () => {
                const currentValue = parseInt(input.value) || 1;
                if (currentValue > 1) {
                    input.value = currentValue - 1;
                    updateTotalPrice(input);
                }
            });
        }
        
        if (incrementBtn) {
            incrementBtn.addEventListener('click', () => {
                const currentValue = parseInt(input.value) || 1;
                const maxStock = parseInt(input.getAttribute('data-max-stock')) || 999;
                
                if (currentValue < maxStock) {
                    input.value = currentValue + 1;
                    updateTotalPrice(input);
                }
            });
        }
        
        input.addEventListener('change', () => {
            updateTotalPrice(input);
        });
    });
}

function updateTotalPrice(quantityInput) {
    const priceElement = quantityInput.closest('.product-card').querySelector('.product-price');
    const totalElement = quantityInput.closest('.product-card').querySelector('.product-total');
    
    if (priceElement && totalElement) {
        const price = parseFloat(priceElement.textContent.replace('$', ''));
        const quantity = parseInt(quantityInput.value) || 1;
        const total = price * quantity;
        
        totalElement.textContent = `$${total.toFixed(2)}`;
    }
}

// Shopping cart functions
function addToCart(productId, productName, price, quantity = 1) {
    let cart = getCart();
    const existingItem = cart.find(item => item.productId === productId);
    
    if (existingItem) {
        existingItem.quantity += quantity;
    } else {
        cart.push({
            productId: productId,
            name: productName,
            price: price,
            quantity: quantity
        });
    }
    
    saveCart(cart);
    updateCartBadge();
    showToast('Product added to cart!', 'success');
}

function removeFromCart(productId) {
    let cart = getCart();
    cart = cart.filter(item => item.productId !== productId);
    saveCart(cart);
    updateCartBadge();
    showToast('Product removed from cart!', 'info');
}

function updateCartQuantity(productId, quantity) {
    let cart = getCart();
    const item = cart.find(item => item.productId === productId);
    
    if (item) {
        if (quantity <= 0) {
            removeFromCart(productId);
        } else {
            item.quantity = quantity;
            saveCart(cart);
            updateCartBadge();
        }
    }
}

function getCart() {
    return JSON.parse(localStorage.getItem('cart')) || [];
}

function saveCart(cart) {
    localStorage.setItem('cart', JSON.stringify(cart));
}

function clearCart() {
    localStorage.removeItem('cart');
    updateCartBadge();
}

function getCartItemCount() {
    const cart = getCart();
    return cart.reduce((total, item) => total + item.quantity, 0);
}

function updateCartBadge() {
    const cartBadges = document.querySelectorAll('.cart-badge');
    const itemCount = getCartItemCount();
    
    cartBadges.forEach(badge => {
        badge.textContent = itemCount;
        badge.style.display = itemCount > 0 ? 'inline-block' : 'none';
    });
}

// Toast notifications
function showToast(message, type = 'info') {
    // Create toast container if it doesn't exist
    let toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        document.body.appendChild(toastContainer);
    }
    
    const toastId = 'toast-' + Date.now();
    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-white bg-${type} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;
    
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
    
    // Remove toast from DOM after it's hidden
    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}

// Price formatting
function formatPrice(price) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(price);
}

// Stock level indicators
function getStockLevelClass(quantity) {
    if (quantity === 0) return 'out-of-stock';
    if (quantity <= 5) return 'low-stock';
    return 'in-stock';
}

function getStockLevelText(quantity) {
    if (quantity === 0) return 'Out of Stock';
    if (quantity <= 5) return `Only ${quantity} left!`;
    return 'In Stock';
}

// API calls
async function apiCall(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('API call failed:', error);
        showToast('An error occurred. Please try again.', 'danger');
        throw error;
    }
}

// Export functions for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        addToCart,
        removeFromCart,
        updateCartQuantity,
        getCart,
        clearCart,
        getCartItemCount,
        formatPrice,
        showToast,
        apiCall
    };
}