package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.CalendarEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link CalendarEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface CalendarRepository extends CrudRepository<CalendarEntity, Long> {

}
