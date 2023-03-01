package com.ticket.shop.converter;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.command.event.EventDetailsWithCalendarIdsDto;
import com.ticket.shop.persistence.entity.EventEntity;

import java.util.List;

/**
 * Event converter
 */
public class EventConverter {

    /**
     * From {@link CreateEventDto} to {@link EventEntity}
     *
     * @param createEventDto {@link CreateEventDto}
     * @return {@link EventEntity}
     */
    public static EventEntity fromCreateEventDtoToEventEntity(CreateEventDto createEventDto) {
        return EventEntity.builder()
                .name(createEventDto.getName())
                .description(createEventDto.getDescription())
                .build();
    }

    /**
     * From {@link EventEntity} to {@link CompanyDetailsDto}
     *
     * @param eventEntity {@link EventEntity}
     * @return {@link CompanyDetailsDto}
     */
    public static EventDetailsDto fromEventEntityToEventDetailsDto(EventEntity eventEntity) {
        return EventDetailsDto.builder()
                .eventId(eventEntity.getEventId())
                .name(eventEntity.getName())
                .description(eventEntity.getDescription())
                .address(AddressConverter.fromAddressEntityToAddressDetailsDto(eventEntity.getAddressEntity()))
                .prices(PriceConverter.fromPriceEntityToPriceDetailsDtoList(eventEntity.getPrices()))
                .build();
    }

    /**
     * From {@link EventEntity} and {@link List<Long>} to {@link EventDetailsWithCalendarIdsDto}
     *
     * @param eventEntity {@link EventEntity}
     * @param calendarIds {@link List<Long>}
     * @return {@link EventDetailsWithCalendarIdsDto}
     */
    public static EventDetailsWithCalendarIdsDto fromEventEntityToEventDetailsWithCalendarIdsDto(EventEntity eventEntity, List<Long> calendarIds) {
        return EventDetailsWithCalendarIdsDto.builder()
                .event(fromEventEntityToEventDetailsDto(eventEntity))
                .calendarIds(calendarIds)
                .build();
    }
}
