package com.ticket.shop.converter;

import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsDto;
import com.ticket.shop.enumerators.TicketStatus;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.TicketEntity;
import com.ticket.shop.persistence.entity.TicketPriceEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Ticket converter
 */
public class TicketConverter {

    /**
     * From {@link List<CreateTicketDto>} to {@link List<TicketEntity>}
     *
     * @param createTicketDto {@link CreateTicketDto}
     * @param calendarEntity  {@link CalendarEntity}
     * @return {@link TicketEntity}
     */
    public static List<TicketEntity> fromListOfCreateTicketDtoToListOfTicketEntity(List<CreateTicketDto> createTicketDto, CalendarEntity calendarEntity) {
        return createTicketDto.stream()
                .flatMap(ticket -> LongStream.range(0, ticket.getTotal())
                        .mapToObj(index -> ticket))
                .map(ticket -> TicketEntity.builder()
                        .type(ticket.getType())
                        .status(TicketStatus.AVAILABLE)
                        .calendarEntity(calendarEntity)
                        .build())
                .toList();
    }

    /**
     * From {@link List<TicketEntity>} to {@link List<TicketDetailsDto>}
     *
     * @param ticketEntities {@link TicketEntity}
     * @return {@link List<TicketDetailsDto>}
     */
    public static List<TicketDetailsDto> fromListOfTicketEntityToListOfTicketDetailsDto(List<TicketEntity> ticketEntities, List<TicketPriceEntity> ticketPrices) {
        Map<TicketType, Double> ticketPriceMap = ticketPrices.stream()
                .collect(Collectors.toMap(TicketPriceEntity::getType, TicketPriceEntity::getValue));

        return ticketEntities.stream()
                .map(ticket -> TicketDetailsDto.builder()
                        .ticketId(ticket.getTicketId())
                        .type(ticket.getType())
                        .status(ticket.getStatus())
                        .price(ticketPriceMap.getOrDefault(ticket.getType(), 0.0))
                        .build())
                .toList();
    }
}
