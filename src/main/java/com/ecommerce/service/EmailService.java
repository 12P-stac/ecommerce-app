package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(User user, Order order) {
        String subject = "Order Confirmation - Ecommerce App";
        String text = "Dear " + user.getUsername() + ",\n\n" +
                "Your order has been placed successfully.\n\nOrder ID: " + order.getId() +
                "\nTotal Amount: " + order.getTotalAmount() +
                "\nStatus: " + order.getStatus() + "\n\nThank you for shopping with us!";
        sendEmail(user.getEmail(), subject, text);
    }

    public void sendOrderStatusUpdate(User user, Order order) {
        String subject = "Order Status Updated - Ecommerce App";
        String text = "Hello " + user.getUsername() + ",\n\n" +
                "Your order #" + order.getId() + " status has been updated to: " + order.getStatus();
        sendEmail(user.getEmail(), subject, text);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
