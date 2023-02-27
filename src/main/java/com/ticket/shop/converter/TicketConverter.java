package com.ticket.shop.converter;

import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsDto;
import com.ticket.shop.command.ticket.TicketDetailsWhenCreatedDto;
import com.ticket.shop.enumerators.TicketStatus;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.TicketEntity;
import com.ticket.shop.persistence.entity.PriceEntity;

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
     * @param createTicketDto {@link List<CreateTicketDto>}
     * @param calendarEntity  {@link CalendarEntity}
     * @return {@link TicketEntity}
     */
    public static List<TicketEntity> fromListOfCreateTicketDtoToListOfTicketEntity(List<CreateTicketDto> createTicketDto, CalendarEntity calendarEntity) {
        return createTicketDto.stream()
                .flatMap(ticket -> LongStream.range(0, ticket.getAmount())
                        .mapToObj(index -> ticket))
                .map(ticket -> TicketEntity.builder()
                        .type(ticket.getType())
                        .status(TicketStatus.AVAILABLE)
                        .calendarEntity(calendarEntity)
                        .companyEntity(calendarEntity.getCompanyEntity())
                        .build())
                .toList();
    }

    /**
     * From {@link List<TicketEntity>} to {@link List<TicketDetailsDto>}
     *
     * @param ticketEntities {@link List<TicketEntity>}
     * @param prices         {@link List<PriceEntity>}
     * @return {@link List<TicketDetailsDto>}
     */
    public static List<TicketDetailsDto> fromListOfTicketEntityToListOfTicketDetailsDto(List<TicketEntity> ticketEntities, List<PriceEntity> prices) {
        Map<TicketType, Double> priceMap = prices.stream()
                .collect(Collectors.toMap(PriceEntity::getType, PriceEntity::getPrice));

        return ticketEntities.stream()
                .map(ticket -> TicketDetailsDto.builder()
                        .ticketId(ticket.getTicketId())
                        .type(ticket.getType())
                        .status(ticket.getStatus())
                        .price(priceMap.getOrDefault(ticket.getType(), 0.0))
                        .build())
                .toList();
    }

    /**
     * From {@link List<TicketEntity>} to {@link List<TicketDetailsWhenCreatedDto>}
     *
     * @param ticketEntities {@link List<TicketEntity>}
     * @param prices         {@link List<PriceEntity>}
     * @return {@link List<TicketDetailsWhenCreatedDto>}
     */
    public static List<TicketDetailsWhenCreatedDto> fromListOfTicketEntityToListOfTicketDetailsWhenCreatedDto(List<TicketEntity> ticketEntities, List<PriceEntity> prices) {
        Map<TicketType, Double> priceMap = prices.stream()
                .collect(Collectors.toMap(PriceEntity::getType, PriceEntity::getPrice));

        Map<TicketType, Long> amountOfTickets = ticketEntities.stream()
                .collect(Collectors.groupingBy(TicketEntity::getType, Collectors.summingLong(t -> 1L)));

        return ticketEntities.stream()
                .map(ticket -> TicketDetailsWhenCreatedDto.builder()
                        .type(ticket.getType())
                        .amountOfTickets(amountOfTickets.get(ticket.getType()))
                        .price(priceMap.getOrDefault(ticket.getType(), 0.0))
                        .build())
                .distinct()
                .toList();
    }
}
