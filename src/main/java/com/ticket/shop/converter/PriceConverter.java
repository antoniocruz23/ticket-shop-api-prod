package com.ticket.shop.converter;

import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.price.CreatePriceDto;
import com.ticket.shop.command.price.PriceDetailsDto;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.PriceEntity;

import java.util.List;

/**
 * Price converter
 */
public class PriceConverter {

    /**
     * From {@link List<CreateTicketDto>} to {@link List<PriceEntity>}
     *
     * @param createPriceDto {@link List<CreatePriceDto>}
     * @param eventEntity {@link EventEntity}
     * @return {@link PriceEntity}
     */
    public static List<PriceEntity> fromListOfCreatePriceDtoToListOfPriceEntity(List<CreatePriceDto> createPriceDto, EventEntity eventEntity) {
        return createPriceDto.stream()
                .map(t -> PriceEntity.builder()
                        .price(t.getPrice())
                        .type(t.getType())
                        .eventEntity(eventEntity)
                        .build())
                .toList();
    }

    /**
     * From {@link PriceEntity} to {@link PriceDetailsDto}
     *
     * @param ticketPriceEntities {@link List<PriceEntity>}
     * @return {@link List<PriceDetailsDto>}
     */
    public static List<PriceDetailsDto> fromPriceEntityToPriceDetailsDtoList(List<PriceEntity> ticketPriceEntities) {
        return ticketPriceEntities.stream()
                .map(price -> PriceDetailsDto.builder()
                        .ticketPriceId(price.getTicketPriceId())
                        .price(price.getPrice())
                        .type(price.getType())
                        .build()).toList();
    }
}
