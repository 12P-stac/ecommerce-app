package com.ecommerce.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class PaymentService {

    public String createPaymentIntent(BigDecimal amount, String currency) {
        // Simulate payment intent creation logic
        // In a real-world scenario, this would involve calling a payment gateway API like Stripe or PayPal
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Simulate a payment intent ID
        return "pi_" + System.currentTimeMillis();
    }
}
