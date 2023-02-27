package com.ticket.shop.persistence.repository;

import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


/**
 * Repository for {@link PriceEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface PriceRepository extends CrudRepository<PriceEntity, Long> {

    /**
     * Get list of prices
     *
     * @param ticketTypes {@link List<TicketType>}
     * @param eventEntity event entity
     * @return {@link List<PriceEntity>}
     */
    @Query("SELECT DISTINCT p FROM PriceEntity p WHERE p.type IN :ticketTypes AND p.eventEntity = :eventEntity")
    List<PriceEntity> findByValuesAndEventEntity(List<TicketType> ticketTypes, EventEntity eventEntity);
}
