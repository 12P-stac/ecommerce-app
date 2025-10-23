package com.ecommerce.service;

import com.ecommerce.model.*;
import com.ecommerce.model.Order.OrderStatus;
import com.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    /**
     * Create a new order from cart items.
     */
    public Order createOrder(User user, List<OrderItem> orderItems, String shippingAddress, String paymentMethod) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();

            // Deduct 5% fee automatically from product price
            BigDecimal priceWithFee = product.getPrice().add(product.getPrice().multiply(new BigDecimal("0.05")));
            orderItem.setPrice(priceWithFee);
            orderItem.setOrder(order);

            // Set product status to SOLD
            product.setStatus(Product.Status.SOLD);
            productService.save(product);

            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING); // Payment after delivery
        return orderRepository.save(order);
    }

    /**
     * Get all orders for a specific user.
     */
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get all orders for a specific seller.
     */
    public List<Order> getSellerOrders(User seller) {
        return orderRepository.findOrdersBySellerId(seller.getId());
    }

    /**
     * Find order by order number.
     */
    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    /**
     * Update order status (admin or seller action).
     */
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found with ID: " + orderId);
    }

    /**
     * Count total orders made by a specific user.
     */
    public Long countUserOrders(User user) {
        return orderRepository.countOrdersByUser(user.getId());
    }

    /**
     * Count pending orders for a specific seller.
     */
    public Long countPendingOrdersBySeller(User seller) {
        return orderRepository.countOrdersBySellerAndStatus(seller.getId(), OrderStatus.PENDING);
    }

    /**
     * Count all orders for a specific seller.
     */
    public Long countAllOrdersBySeller(User seller) {
        return orderRepository.countOrdersBySellerAndStatus(seller.getId(), OrderStatus.PROCESSING)
             + orderRepository.countOrdersBySellerAndStatus(seller.getId(), OrderStatus.SHIPPED)
             + orderRepository.countOrdersBySellerAndStatus(seller.getId(), OrderStatus.DELIVERED)
             + orderRepository.countOrdersBySellerAndStatus(seller.getId(), OrderStatus.CANCELLED);
    }

    /**
     * Save or update an order.
     */
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public void updateOrderStatus(Long orderId, String status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void createOrder(User user, BigDecimal totalAmount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createOrder'");
    }

    public List<Order> getOrderByNumber(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOrderByNumber'");
    }

    public List<Order> getOrdersByStatus(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOrdersByStatus'");
    }

    public void saveOrderItem(OrderItem orderItem) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveOrderItem'");
    }

}
