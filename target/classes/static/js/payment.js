// payment.js - Stripe payment handling

const stripe = Stripe('pk_test_your_stripe_public_key_here');
const elements = stripe.elements();
const cardElement = elements.create('card');
cardElement.mount('#card-element');

const cardErrors = document.getElementById('card-errors');
const submitButton = document.getElementById('submit-button');
const paymentForm = document.getElementById('payment-form');

cardElement.on('change', ({error}) => {
    if (error) {
        cardErrors.textContent = error.message;
    } else {
        cardErrors.textContent = '';
    }
});

paymentForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    
    submitButton.disabled = true;
    submitButton.textContent = 'Processing...';

    const {paymentIntent, error} = await stripe.confirmCardPayment(
        await createPaymentIntent(), {
            payment_method: {
                card: cardElement,
            }
        }
    );

    if (error) {
        cardErrors.textContent = error.message;
        submitButton.disabled = false;
        submitButton.textContent = `Pay $${document.getElementById('total-amount').textContent}`;
    } else {
        // Payment successful
        window.location.href = `/payment/success?paymentIntent=${paymentIntent.id}`;
    }
});

async function createPaymentIntent() {
    const totalAmount = document.getElementById('total-amount').textContent;
    
    const response = await fetch('/payment/create-payment-intent', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `amount=${totalAmount}`
    });
    
    const {clientSecret} = await response.json();
    return clientSecret;
}

// Load cart items and calculate total
function loadOrderSummary() {
    // This would typically fetch cart data from your backend or session
    const cartItems = JSON.parse(localStorage.getItem('cart') || '[]');
    const orderItemsContainer = document.getElementById('order-items');
    let total = 0;

    cartItems.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        
        const itemElement = document.createElement('div');
        itemElement.className = 'd-flex justify-content-between mb-2';
        itemElement.innerHTML = `
            <span>${item.name} x${item.quantity}</span>
            <span>$${itemTotal.toFixed(2)}</span>
        `;
        orderItemsContainer.appendChild(itemElement);
    });

    document.getElementById('order-total').textContent = `$${total.toFixed(2)}`;
    document.getElementById('total-amount').textContent = total.toFixed(2);
}

// Initialize when page loads
document.addEventListener('DOMContentLoaded', loadOrderSummary);