package com.ticket.shop.command.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

/**
 * OrderDetailsDto used to respond with PayPal order details
 */
@Data
@AllArgsConstructor
public class OrderDetailsDto {
    private String orderId;
    private URI paymentLink;
}
