package com.ticket.shop.command.event;

import com.ticket.shop.command.prices.PricesDetailsDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * EventDetailsDto used to store event info when created
 */
@Data
@Builder
public class CreateEventDto {
    private String name;
    private String address;
    private String description;
    private List<PricesDetailsDto> eventPrices;
}
