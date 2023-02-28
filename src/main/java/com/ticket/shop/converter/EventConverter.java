package com.ticket.shop.converter;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.command.event.EventDetailsWithCalendarIdsDto;
import com.ticket.shop.command.price.PriceDetailsDto;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.PriceEntity;

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
     * From {@link EventEntity} and {@link List<PriceDetailsDto>} to {@link CompanyDetailsDto}
     *
     * @param eventEntity {@link EventEntity}
     * @param prices      {@link List<PriceDetailsDto>}
     * @return {@link CompanyDetailsDto}
     */
    public static EventDetailsDto fromEventEntityToEventDetailsDto(EventEntity eventEntity, List<PriceDetailsDto> prices) {
        return EventDetailsDto.builder()
                .eventId(eventEntity.getEventId())
                .name(eventEntity.getName())
                .description(eventEntity.getDescription())
                .address(AddressConverter.fromAddressEntityToAddressDetailsDto(eventEntity.getAddressEntity()))
                .prices(prices)
                .build();
    }

    /**
     * From {@link EventEntity}, {@link List<PriceDetailsDto>} and {@link List<Long>} to {@link EventDetailsWithCalendarIdsDto}
     *
     * @param eventEntity {@link EventEntity}
     * @param prices      {@link List<PriceDetailsDto>}
     * @param calendarIds {@link List<Long>}
     * @return {@link EventDetailsWithCalendarIdsDto}
     */
    public static EventDetailsWithCalendarIdsDto fromEventEntityToEventDetailsWithCalendarIdsDto(EventEntity eventEntity, List<PriceEntity> prices, List<Long> calendarIds) {
        List<PriceDetailsDto> priceDetailsDto = PriceConverter.fromPriceEntityToPriceDetailsDtoList(prices);

        return EventDetailsWithCalendarIdsDto.builder()
                .event(fromEventEntityToEventDetailsDto(eventEntity, priceDetailsDto))
                .calendarIds(calendarIds)
                .build();
    }
}
