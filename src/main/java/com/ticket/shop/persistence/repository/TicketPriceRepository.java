package com.ticket.shop.persistence.repository;

import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.persistence.entity.TicketPriceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


/**
 * Repository for {@link TicketPriceEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface TicketPriceRepository extends CrudRepository<TicketPriceEntity, Long> {

    /**
     * Get list of ticket prices
     *
     * @param ticketTypes {@link List<TicketType>}
     * @param eventId event id
     * @return {@link List<TicketPriceEntity>}
     */
    @Query("SELECT DISTINCT p.type, p.value FROM TicketPriceEntity p WHERE p.type IN :ticketTypes AND p.eventEntity.eventId = :eventId")
    List<TicketPriceEntity> findByValuesAndEventId(List<TicketType> ticketTypes, Long eventId);
}
