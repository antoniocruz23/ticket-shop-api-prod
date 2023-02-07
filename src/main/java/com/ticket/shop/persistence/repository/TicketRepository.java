package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.TicketEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link TicketEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface TicketRepository extends CrudRepository<TicketEntity, Long> {

}
