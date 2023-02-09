package com.ticket.shop.command.event;

import com.ticket.shop.command.address.AddressDetailsDto;
import lombok.Builder;
import lombok.Data;

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
}
