package com.ticket.shop.command.event;

import com.ticket.shop.command.prices.PricesDetailsDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * EventDetailsDto used to respond with event details
 */
@Data
@Builder
public class EventDetailsDto {
    private Long eventId;
    private String name;
    private String address;
    private String description;
    private List<PricesDetailsDto> eventPrices;
}
