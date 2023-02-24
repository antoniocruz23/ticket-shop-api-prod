package com.ticket.shop.service;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import com.ticket.shop.command.order.CreateOrderDto;
import com.ticket.shop.command.order.OrderDetailsDto;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.order.OrderCaptureException;
import com.ticket.shop.exception.calendar.CalendarNotFoundException;
import com.ticket.shop.exception.event.EventNotFoundException;
import com.ticket.shop.exception.order.PayPalOrderException;
import com.ticket.shop.exception.user.UserNotFoundException;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.persistence.repository.EventRepository;
import com.ticket.shop.persistence.repository.TicketRepository;
import com.ticket.shop.persistence.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OrderServiceImp implements OrderService {

    private static final Logger LOGGER = LogManager.getLogger(OrderServiceImp.class);
    private final PayPalHttpClient paypalHttpClient;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public OrderServiceImp(@Value("${paypal.client.id}") String clientId,
                           @Value("${paypal.client.secret}") String clientSecret,
                           EventRepository eventRepository, TicketRepository ticketRepository,
                           UserRepository userRepository) {

        this.paypalHttpClient = new PayPalHttpClient(new PayPalEnvironment.Sandbox(clientId, clientSecret));
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    /**
     * @see OrderService#createOrder(CreateOrderDto, String)
     */
    public OrderDetailsDto createOrder(CreateOrderDto createOrderDto, String requestUrl) {

        // Get All Entities needed
        EventEntity eventEntity = getEventById(createOrderDto.getEventId());
        CalendarEntity calendarEntity = getCalendarFromEventEntity(eventEntity, createOrderDto.getCalendarId());
        UserEntity userEntity = getUserById(createOrderDto.getCustomerId());

        URI returnUri = buildReturnUrl(requestUrl);

        // Create PayPal order
        PayPalData payPalOrder = createPayPalOrder(createOrderDto.getTotalAmount(), userEntity.getCountryEntity().getCurrency(), returnUri);

        // Finding tickets available for the event and calendar
        List<Long> availableTicketIds = getAvailableTicketIdsToBuy(calendarEntity.getCalendarId(), createOrderDto.getTicketType(), createOrderDto.getNumberOfTickets());

        // Update the tickets found
        updateTicketWithUserAndStatus(userEntity, availableTicketIds, payPalOrder.orderId());

        return new OrderDetailsDto(payPalOrder.orderId(), URI.create(payPalOrder.approveUri().href()));
    }

    /**
     * @see OrderService#captureOrder(String)
     */
    @Override
    public String captureOrder(String orderId) {
        String orderStatus = requestOrderCapture(orderId);

        if (!Objects.equals(orderStatus, "COMPLETED")) {
            return orderStatus;
        }

        LOGGER.debug("Updating tickets from order id {} as COMPLETE status", orderId);
        try {
            this.ticketRepository.updateStatusByPaypalOrderId(orderId);

        } catch (Exception e) {
            LOGGER.error("Failed while updating ticket from order id {} - ", orderId, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        return orderStatus;
    }

    /**
     * Request order capture
     *
     * @param orderId order id
     * @return order status
     */
    private String requestOrderCapture(String orderId) {
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(orderId);

        LOGGER.debug("Requesting capture of order id {}", orderId);
        HttpResponse<Order> httpResponse;
        try {
            httpResponse = this.paypalHttpClient.execute(ordersCaptureRequest);

        } catch (IOException e) {
            String issue = extractErrorMessage(e);

            LOGGER.error("Failed while requesting the capture of order id {} - ", orderId, e);
            throw new OrderCaptureException(issue, e);
        }

        String orderStatus = httpResponse.result().status();
        LOGGER.info("Order with id {} have the capture Status: {}", orderId, orderStatus);
        return orderStatus;
    }

    /**
     * Build return Uri
     *
     * @param requestUrl request url
     * @return {@link URI}
     */
    private URI buildReturnUrl(String requestUrl) {
        LOGGER.info("Build return url - {}", requestUrl);
        //NOTE -> This return url could be use for redirect a FE page
        try {
            URI requestUri = URI.create(requestUrl);
            return new URI(requestUri.getScheme(),
                    requestUri.getUserInfo(),
                    requestUri.getHost(),
                    requestUri.getPort(),
                    "/api/paypal/capture",
                    null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create PayPal order to receive the Uri for the payment
     *
     * @param totalPrice total price of all tickets
     * @param currency   currency
     * @param returnUrl  return Url
     * @return {@link PayPalData}
     */
    private PayPalData createPayPalOrder(Double totalPrice, String currency, URI returnUrl) {
        OrderRequest orderRequest = createOrderRequest(totalPrice, currency, returnUrl);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

        LOGGER.debug("Creating PayPal order with - {} price, {} currency", totalPrice, currency);
        HttpResponse<Order> orderHttpResponse;
        try {
            orderHttpResponse = this.paypalHttpClient.execute(ordersCreateRequest);

        } catch (IOException e) {
            String issue = extractErrorMessage(e);

            LOGGER.error("Failed while creating PayPal", e);
            throw new PayPalOrderException(issue, e);
        }

        Order order = orderHttpResponse.result();
        LinkDescription approveUri = extractApprovalLink(order);

        return new PayPalData(order.id(), approveUri);
    }

    private static String extractErrorMessage(IOException e) {
        Pattern pattern = Pattern.compile("\"issue\":\"(\\w+)\"");
        Matcher matcher = pattern.matcher(e.getMessage());
        String issue = "";
        if (matcher.find()) {
            issue = matcher.group(1);
        }
        return issue;
    }

    private record PayPalData(String orderId, LinkDescription approveUri) {
    }

    /**
     * Create Order request
     *
     * @param totalAmount  total amount
     * @param currencyCode currency code
     * @param returnUrl    {@link URI}
     * @return {@link OrderRequest}
     */
    private OrderRequest createOrderRequest(Double totalAmount, String currencyCode, URI returnUrl) {
        LOGGER.info("Creating order request with values - {} total amount, {} currency code, {} return url",
                totalAmount, currencyCode, returnUrl);

        OrderRequest orderRequest = new OrderRequest();
        setCheckoutIntent(orderRequest);
        setPurchaseUnits(totalAmount, currencyCode, orderRequest);
        setApplicationContext(returnUrl, orderRequest);
        return orderRequest;
    }

    /**
     * Set checkout payment to CAPTURE
     *
     * @param orderRequest {@link OrderRequest}
     */
    private void setCheckoutIntent(OrderRequest orderRequest) {
        orderRequest.checkoutPaymentIntent("CAPTURE");
    }

    /**
     * Set amount to be charged and Currency Code
     *
     * @param totalAmount  total amount
     * @param currencyCode currency code
     * @param orderRequest {@link OrderRequest}
     */
    private void setPurchaseUnits(Double totalAmount, String currencyCode, OrderRequest orderRequest) {
        PurchaseUnitRequest purchaseUnitRequest =
                new PurchaseUnitRequest().amountWithBreakdown(new AmountWithBreakdown().currencyCode(currencyCode).value(totalAmount.toString()));

        orderRequest.purchaseUnits(Collections.singletonList(purchaseUnitRequest));
    }

    /**
     * Set Return Url on Order request
     *
     * @param returnUrl    {@link URI}
     * @param orderRequest {@link OrderRequest}
     */
    private void setApplicationContext(URI returnUrl, OrderRequest orderRequest) {
        orderRequest.applicationContext(new ApplicationContext().returnUrl(returnUrl.toString()));
    }

    /**
     * Extract Approval link from {@link Order}
     *
     * @param order {@link Order}
     * @return {@link LinkDescription} approval link
     */
    private LinkDescription extractApprovalLink(Order order) {
        return order.links().stream()
                .filter(link -> "approve".equals(link.rel()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Get event by id
     *
     * @param eventId event id
     * @return {@link EventEntity}
     */
    private EventEntity getEventById(Long eventId) {
        LOGGER.debug("Getting event with id {} from database", eventId);
        return this.eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    LOGGER.error("The event with id {} does not exist in database", eventId);
                    return new EventNotFoundException(ErrorMessages.EVENT_NOT_FOUND);
                });
    }


    /**
     * Get Calendar from EventEntity
     *
     * @param eventEntity event entity
     * @param calendarId  calendar id
     * @return {@link CalendarEntity}
     */
    private CalendarEntity getCalendarFromEventEntity(EventEntity eventEntity, Long calendarId) {
        return eventEntity.getCalendars().stream()
                .filter(c -> c.getCalendarId().equals(calendarId))
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("The calendar with id {} does not exist in database", calendarId);
                    return new CalendarNotFoundException(ErrorMessages.CALENDAR_NOT_FOUND);
                });
    }

    /**
     * Update tickets with user and WAITING_PAYMENT status
     *
     * @param userEntity user that will buy the ticket
     * @param ticketIds  ticket ids
     */
    private void updateTicketWithUserAndStatus(UserEntity userEntity, List<Long> ticketIds, String orderId) {
        LOGGER.debug("Updating ticket ids {} with user id {} and order id {} from database", ticketIds, userEntity.getUserId(), orderId);
        try {
            this.ticketRepository.updateUserEntityAndStatusByCalendarEntityAndType(userEntity, ticketIds, orderId);

        } catch (Exception e) {
            LOGGER.error("Failed while updating ticket ids {} - ", ticketIds, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }
    }

    /**
     * Get User by id
     *
     * @param userId user id
     * @return {@link UserEntity}
     */
    private UserEntity getUserById(Long userId) {
        LOGGER.debug("Getting user with id {} from database", userId);
        return this.userRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.error("The user with id {} does not exist in database", userId);
                    return new UserNotFoundException(ErrorMessages.USER_NOT_FOUND);
                });
    }

    /**
     * Get Available tickets ids to Buy
     *
     * @param calendarId   calendar id
     * @param type         ticket type
     * @param totalTickets total of tickets
     * @return {@link List<Long>}
     */
    private List<Long> getAvailableTicketIdsToBuy(Long calendarId, TicketType type, Long totalTickets) {
        LOGGER.debug("Getting available tickets from database");
        return this.ticketRepository.findAvailableByCalendarAndType(calendarId, type.name(), totalTickets);
    }
}
