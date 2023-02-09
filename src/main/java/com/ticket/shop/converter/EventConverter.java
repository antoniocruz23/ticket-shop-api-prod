package com.ticket.shop.converter;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.persistence.entity.EventEntity;

/**
 * Event converter
 */
public class EventConverter {

    /**
     * From {@link CreateEventDto} to {@link EventEntity}
     * @param createEventDto {@link CreateEventDto}
     * @return {@link EventEntity}
     */
    public static EventEntity fromCreateCompanyDtoToCompanyEntity(CreateEventDto createEventDto) {
        return EventEntity.builder()
                .name(createEventDto.getName())
                .description(createEventDto.getDescription())
                .build();
    }

    /**
     * From {@link EventEntity} to {@link CompanyDetailsDto}
     * @param eventEntity {@link EventEntity}
     * @return {@link CompanyDetailsDto}
     */
    public static EventDetailsDto fromCompanyEntityToCompanyDetailsDto(EventEntity eventEntity) {
        return EventDetailsDto.builder()
                .eventId(eventEntity.getEventId())
                .name(eventEntity.getName())
                .description(eventEntity.getDescription())
                .address(AddressConverter.fromAddressEntityToAddressDetailsDto(eventEntity.getAddressEntity()))
                .build();
    }
}