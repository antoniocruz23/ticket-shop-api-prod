package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for {@link CalendarEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface CalendarRepository extends CrudRepository<CalendarEntity, Long> {

    /**
     * Get page of workers by user entity
     *
     * @param eventEntity event entity
     * @param pageable    pageable
     * @return {@link Page<CalendarRepository>}
     */
    Page<CalendarEntity> findByEventEntity(EventEntity eventEntity, Pageable pageable);

    Optional<CalendarEntity> findByCalendarIdAndCompanyEntity(Long calendarId, CompanyEntity companyEntity);
}
