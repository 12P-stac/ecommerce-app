// cart.js - Shopping cart functionality

class ShoppingCart {
    constructor() {
        this.cartKey = 'shopping_cart';
        this.init();
    }
    
    init() {
        this.updateCartDisplay();
        this.attachEventListeners();
    }
    
    // Get cart from localStorage
    getCart() {
        return JSON.parse(localStorage.getItem(this.cartKey)) || [];
    }
    
    // Save cart to localStorage
    saveCart(cart) {
        localStorage.setItem(this.cartKey, JSON.stringify(cart));
        this.updateCartDisplay();
    }
    
    // Add item to cart
    addItem(productId, productName, price, quantity = 1, imageUrl = '') {
        const cart = this.getCart();
        const existingItemIndex = cart.findIndex(item => item.productId === productId);
        
        if (existingItemIndex > -1) {
            // Update existing item
            cart[existingItemIndex].quantity += quantity;
        } else {
            // Add new item
            cart.push({
                productId,
                name: productName,
                price: parseFloat(price),
                quantity: parseInt(quantity),
                imageUrl,
                addedAt: new Date().toISOString()
            });
        }
        
        this.saveCart(cart);
        this.showNotification('Product added to cart!', 'success');
        return cart;
    }
    
    // Remove item from cart
    removeItem(productId) {
        const cart = this.getCart().filter(item => item.productId !== productId);
        this.saveCart(cart);
        this.showNotification('Product removed from cart!', 'info');
        return cart;
    }
    
    // Update item quantity
    updateQuantity(productId, quantity) {
        const cart = this.getCart();
        const itemIndex = cart.findIndex(item => item.productId === productId);
        
        if (itemIndex > -1) {
            if (quantity <= 0) {
                return this.removeItem(productId);
            } else {
                cart[itemIndex].quantity = parseInt(quantity);
                this.saveCart(cart);
            }
        }
        
        return cart;
    }
    
    // Clear entire cart
    clearCart() {
        localStorage.removeItem(this.cartKey);
        this.updateCartDisplay();
        this.showNotification('Cart cleared!', 'info');
    }
    
    // Get total number of items in cart
    getTotalItems() {
        return this.getCart().reduce((total, item) => total + item.quantity, 0);
    }
    
    // Get total price of cart
    getTotalPrice() {
        return this.getCart().reduce((total, item) => total + (item.price * item.quantity), 0);
    }
    
    // Check if cart is empty
    isEmpty() {
        return this.getCart().length === 0;
    }
    
    // Update cart display throughout the site
    updateCartDisplay() {
        const cart = this.getCart();
        const totalItems = this.getTotalItems();
        const totalPrice = this.getTotalPrice();
        
        // Update cart badges
        document.querySelectorAll('.cart-badge').forEach(badge => {
            badge.textContent = totalItems;
            badge.style.display = totalItems > 0 ? 'inline-block' : 'none';
        });
        
        // Update cart total displays
        document.querySelectorAll('.cart-total').forEach(element => {
            element.textContent = this.formatPrice(totalPrice);
        });
        
        // Update cart page if open
        this.updateCartPage(cart);
    }
    
    // Update cart page with current items
    updateCartPage(cart) {
        const cartContainer = document.getElementById('cart-items');
        const emptyCartMessage = document.getElementById('empty-cart-message');
        const cartSummary = document.getElementById('cart-summary');
        
        if (cartContainer && emptyCartMessage && cartSummary) {
            if (this.isEmpty()) {
                cartContainer.style.display = 'none';
                emptyCartMessage.style.display = 'block';
                cartSummary.style.display = 'none';
            } else {
                cartContainer.style.display = 'block';
                emptyCartMessage.style.display = 'none';
                cartSummary.style.display = 'block';
                
                this.renderCartItems(cart, cartContainer);
                this.updateCartSummary();
            }
        }
    }
    
    // Render cart items in cart page
    renderCartItems(cart, container) {
        container.innerHTML = '';
        
        cart.forEach(item => {
            const itemElement = this.createCartItemElement(item);
            container.appendChild(itemElement);
        });
    }
    
    // Create HTML for a cart item
    createCartItemElement(item) {
        const itemTotal = item.price * item.quantity;
        const element = document.createElement('div');
        element.className = 'cart-item row align-items-center mb-3 pb-3 border-bottom';
        element.innerHTML = `
            <div class="col-md-2">
                ${item.imageUrl ? 
                    `<img src="${item.imageUrl}" alt="${item.name}" class="img-fluid rounded">` :
                    '<div class="bg-light rounded d-flex align-items-center justify-content-center" style="height: 80px;">No Image</div>'
                }
            </div>
            <div class="col-md-4">
                <h6 class="mb-1">${item.name}</h6>
                <p class="text-muted mb-0">$${item.price.toFixed(2)} each</p>
            </div>
            <div class="col-md-3">
                <div class="input-group input-group-sm">
                    <button class="btn btn-outline-secondary quantity-decrement" type="button">-</button>
                    <input type="number" class="form-control text-center quantity-input" 
                           value="${item.quantity}" min="1" max="99" 
                           data-product-id="${item.productId}">
                    <button class="btn btn-outline-secondary quantity-increment" type="button">+</button>
                </div>
            </div>
            <div class="col-md-2">
                <strong class="item-total">$${itemTotal.toFixed(2)}</strong>
            </div>
            <div class="col-md-1">
                <button class="btn btn-outline-danger btn-sm remove-item" 
                        data-product-id="${item.productId}" title="Remove item">
                    ×
                </button>
            </div>
        `;
        
        return element;
    }
    
    // Update cart summary
    updateCartSummary() {
        const subtotal = this.getTotalPrice();
        const shipping = subtotal > 50 ? 0 : 5.99; // Free shipping over $50
        const tax = subtotal * 0.08; // 8% tax
        const total = subtotal + shipping + tax;
        
        document.querySelectorAll('.cart-subtotal').forEach(el => {
            el.textContent = this.formatPrice(subtotal);
        });
        
        document.querySelectorAll('.cart-shipping').forEach(el => {
            el.textContent = shipping === 0 ? 'FREE' : this.formatPrice(shipping);
        });
        
        document.querySelectorAll('.cart-tax').forEach(el => {
            el.textContent = this.formatPrice(tax);
        });
        
        document.querySelectorAll('.cart-total').forEach(el => {
            el.textContent = this.formatPrice(total);
        });
    }
    
    // Attach event listeners
    attachEventListeners() {
        // Delegate events for dynamic content
        document.addEventListener('click', (e) => {
            // Add to cart buttons
            if (e.target.classList.contains('add-to-cart') || 
                e.target.closest('.add-to-cart')) {
                const button = e.target.classList.contains('add-to-cart') ? e.target : e.target.closest('.add-to-cart');
                this.handleAddToCart(button);
            }
            
            // Remove item buttons
            if (e.target.classList.contains('remove-item') || 
                e.target.closest('.remove-item')) {
                const button = e.target.classList.contains('remove-item') ? e.target : e.target.closest('.remove-item');
                const productId = button.dataset.productId;
                this.removeItem(productId);
            }
            
            // Clear cart button
            if (e.target.classList.contains('clear-cart') || 
                e.target.closest('.clear-cart')) {
                this.clearCart();
            }
        });
        
        // Quantity changes
        document.addEventListener('change', (e) => {
            if (e.target.classList.contains('quantity-input')) {
                const input = e.target;
                const productId = input.dataset.productId;
                const quantity = parseInt(input.value) || 1;
                this.updateQuantity(productId, quantity);
            }
        });
        
        // Quantity increment/decrement buttons
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('quantity-increment') || 
                e.target.classList.contains('quantity-decrement')) {
                const button = e.target;
                const input = button.closest('.input-group').querySelector('.quantity-input');
                const productId = input.dataset.productId;
                let quantity = parseInt(input.value) || 1;
                
                if (button.classList.contains('quantity-increment')) {
                    quantity++;
                } else if (button.classList.contains('quantity-decrement') && quantity > 1) {
                    quantity--;
                }
                
                input.value = quantity;
                this.updateQuantity(productId, quantity);
            }
        });
    }
    
    // Handle add to cart button click
    handleAddToCart(button) {
        const productId = button.dataset.productId;
        const productName = button.dataset.productName;
        const price = parseFloat(button.dataset.price);
        const quantity = parseInt(button.dataset.quantity) || 1;
        const imageUrl = button.dataset.imageUrl || '';
        
        this.addItem(productId, productName, price, quantity, imageUrl);
    }
    
    // Show notification
    showNotification(message, type = 'info') {
        // Use the toast function from app.js if available
        if (typeof showToast === 'function') {
            showToast(message, type);
        } else {
            // Fallback notification
            alert(message);
        }
    }
    
    // Format price
    formatPrice(price) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(price);
    }
}

// Initialize shopping cart when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.shoppingCart = new ShoppingCart();
});

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ShoppingCart;
}