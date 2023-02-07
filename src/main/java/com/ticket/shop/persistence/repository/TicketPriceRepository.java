package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.TicketPriceEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link TicketPriceEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface TicketPriceRepository extends CrudRepository<TicketPriceEntity, Long> {

}
