package com.ticket.shop.service;

import com.ticket.shop.command.price.CreatePriceDto;
import com.ticket.shop.command.price.PriceDetailsDto;
import com.ticket.shop.converter.PriceConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.PriceEntity;
import com.ticket.shop.persistence.repository.PriceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An {@link PriceService} implementation
 */
@Service
public class PriceServiceImp implements PriceService {

    private static final Logger LOGGER = LogManager.getLogger(EventService.class);
    private final PriceRepository priceRepository;

    public PriceServiceImp(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    /**
     * @see PriceService#bulkCreatePrices(List, EventEntity)
     */
    @Override
    public List<PriceDetailsDto> bulkCreatePrices(List<CreatePriceDto> createPriceDto, EventEntity eventEntity) {
        LOGGER.debug("Creating prices - {}", createPriceDto);
        List<PriceEntity> priceEntityList = PriceConverter.fromListOfCreatePriceDtoToListOfPriceEntity(createPriceDto, eventEntity);

        LOGGER.info("Persisting prices into database");
        Iterable<PriceEntity> createdTicketPrice;
        try {
            LOGGER.info("Saving prices on database");
            createdTicketPrice = this.priceRepository.saveAll(priceEntityList);

        } catch (Exception e) {
            LOGGER.error("Failed while saving prices into database {}", priceEntityList, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Retrieving created prices");
        List<PriceEntity> prices = StreamSupport.stream(createdTicketPrice.spliterator(), false).collect(Collectors.toList());
        eventEntity.setPrices(prices);
        return PriceConverter.fromPriceEntityToPriceDetailsDtoList(prices);
    }
}
