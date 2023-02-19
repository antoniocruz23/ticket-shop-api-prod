package com.ticket.shop.command.event;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.price.PriceDetailsDto;
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
    private String description;
    private AddressDetailsDto address;
    private List<PriceDetailsDto> prices;
}
