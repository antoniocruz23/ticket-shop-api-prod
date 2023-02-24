package com.ticket.shop.service;

import com.ticket.shop.command.order.CreateOrderDto;
import com.ticket.shop.command.order.OrderDetailsDto;

/**
 * Common interface for order services, provides methods to manage orders
 */
public interface OrderService {

    /**
     * Create new Order
     *
     * @param createOrderDto {@link CreateOrderDto}
     * @param requestUrl     request url
     * @return {@link OrderDetailsDto}
     */
    OrderDetailsDto createOrder(CreateOrderDto createOrderDto, String requestUrl);

    /**
     * Capture Order
     *
     * @param orderId order id
     * @return order status
     */
    String captureOrder(String orderId);
}
