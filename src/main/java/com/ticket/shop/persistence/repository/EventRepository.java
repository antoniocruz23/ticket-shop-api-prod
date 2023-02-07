package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.EventEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link EventEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface EventRepository extends CrudRepository<EventEntity, Long> {

}
