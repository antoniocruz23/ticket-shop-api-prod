package com.ticket.shop.service;

import com.ticket.shop.command.price.CreatePriceDto;
import com.ticket.shop.command.price.PriceDetailsDto;
import com.ticket.shop.persistence.entity.EventEntity;

import java.util.List;

/**
 * Common interface for price services, provides methods to manage prices
 */
public interface PriceService {

    /**
     * Bulk create new prices
     *
     * @param createPriceDto {@link List<CreatePriceDto>}
     * @param eventEntity    {@link EventEntity}
     * @return {@link PriceDetailsDto} the price created
     */
    List<PriceDetailsDto> bulkCreatePrices(List<CreatePriceDto> createPriceDto, EventEntity eventEntity);
}
