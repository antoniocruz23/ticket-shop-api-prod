package com.ticket.shop.controller;

import com.ticket.shop.command.order.CreateOrderDto;
import com.ticket.shop.command.order.OrderDetailsDto;
import com.ticket.shop.error.Error;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.service.OrderServiceImp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * REST controller responsible for PayPal sdk operations
 */
@Controller
@RequestMapping("/api/paypal/orders")
@Tag(name = "PayPal", description = "PayPal requests to make a order and payment")
public class OrderController {

    private static final Logger LOGGER = LogManager.getLogger(OrderController.class);
    private final OrderServiceImp orderServiceImp;

    public OrderController(OrderServiceImp orderServiceImp) {
        this.orderServiceImp = orderServiceImp;
    }

    @PostMapping
    @PreAuthorize("@authorized.hasRole('ADMIN') || (@authorized.hasRole('CUSTOMER') && @authorized.isUser(#createOrderDto.customerId))")
    @Operation(summary = "Create Order",
            description = "Create Order and provide payment link - Restrict for users with 'CUSTOMER' role and the logged in user id needs to be the same as the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(schema = @Schema(implementation = OrderDetailsDto.class))),
            @ApiResponse(responseCode = "422", description = "The given number for numberOfTickets and totalPrice must be greater than or equal to 1",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.EVENT_NOT_FOUND + " || " + ErrorMessages.CALENDAR_NOT_FOUND + " || "
                    + ErrorMessages.USER_NOT_FOUND + " || " + ErrorMessages.TICKET_UNAVAILABLE,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR + " || " + "PayPal Order Error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<OrderDetailsDto> createOrder(@Valid @RequestBody CreateOrderDto createOrderDto,
                                                       HttpServletRequest request) {

        LOGGER.info("Request to create new order - {}", createOrderDto);
        OrderDetailsDto createdOrder;
        try {
            createdOrder = this.orderServiceImp.createOrder(createOrderDto, request.getRequestURL().toString());

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to create order - {}", createOrderDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }
        LOGGER.info("Order created successfully. Retrieving link to the payment {}", createdOrder.getPaymentLink());
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/capture")
    @Operation(summary = "Capture Order", description = "Capture Order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Captured"),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR + " || " + "Capture Error",
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<String> captureOrder(@RequestParam String token) {

        LOGGER.info("Capture order id - {}", token);
        String orderStatus;
        try {
            orderStatus = this.orderServiceImp.captureOrder(token);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to capture order id - {}", token, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }
        return new ResponseEntity<>(orderStatus, HttpStatus.OK);
    }
}
