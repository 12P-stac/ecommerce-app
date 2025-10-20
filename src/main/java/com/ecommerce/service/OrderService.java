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
    
    public Order createOrder(User user, List<CartItem> cartItems, String shippingAddress, String paymentMethod) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            
            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }
        
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }
    
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public List<Order> getSellerOrders(User seller) {
        return orderRepository.findOrdersBySellerId(seller.getId());
    }
    
    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    public Order updateOrderStatus(Long orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }
    
    public Long countUserOrders(User user) {
        return orderRepository.countOrdersByUser(user.getId());
    }
    
    public Long countPendingOrdersBySeller(User seller) {
        return orderRepository.countOrdersBySellerAndStatus(seller.getId(), OrderStatus.PENDING);
    }

    public void createOrder(User user, BigDecimal totalAmount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createOrder'");
    }

    public List<Order> getOrdersByStatus(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOrdersByStatus'");
    }
public List<Order> getOrderByNumber(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOrderByNumber'");
    }

public Object countAllOrdersBySeller(User seller) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'countAllOrdersBySeller'");
}
public Order saveOrder(Order order) {
    return orderRepository.save(order);
}

public void saveOrderItem(OrderItem orderItem) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'saveOrderItem'");
}
}