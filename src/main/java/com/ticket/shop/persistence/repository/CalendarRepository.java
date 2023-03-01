package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * Get Calendar by id and company Entity
     *
     * @param calendarId    calendar id
     * @param companyEntity company entity
     * @return {@link Optional<CalendarEntity>}
     */
    Optional<CalendarEntity> findByCalendarIdAndCompanyEntity(Long calendarId, CompanyEntity companyEntity);

    /**
     * Get Calendar by calendar id and event entity and company id
     *
     * @param calendarId  calendar id
     * @param eventEntity event entity
     * @param companyId   company id
     * @return {@link Optional<CalendarEntity>}
     */
    Optional<CalendarEntity> findByCalendarIdAndEventEntityAndCompanyEntityCompanyId(Long calendarId, EventEntity eventEntity, Long companyId);

    /**
     * Get calendar by company id and calendar id
     *
     * @param companyId  company id
     * @param calendarId calendar id
     * @return {@link Optional<CalendarEntity>}
     */
    @Query("select e from CalendarEntity e where e.companyEntity.companyId = :companyId and e.calendarId = :calendarId")
    Optional<CalendarEntity> findByCompanyIdAndCalendarId(Long companyId, Long calendarId);
}
