package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.Optional;

/**
 * Repository for {@link EventEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface EventRepository extends CrudRepository<EventEntity, Long> {

    /**
     * Get event by company id and event id
     *
     * @param companyId company id
     * @param eventId   event id
     * @return {@link Optional<EventEntity>}
     */
    @Query("select e from EventEntity e where e.companyEntity.companyId = :companyId and e.eventId = :eventId")
    Optional<EventEntity> findByCompanyIdAndEventId(Long companyId, Long eventId);

    /**
     * Get all events by company id and/or date
     * Also can get without the company id or date
     *
     * @param companyId company id
     * @param date      date
     * @param pageable  pageable
     * @return {@link Page<EventEntity>}
     */
    @Query(value = """
            SELECT e.*
            FROM events e
                LEFT JOIN calendars ca ON ca.event_id = e.event_id
            WHERE (e.company_id = :companyId OR :companyId IS NULL)
            AND ((ca.start_date >= :date OR CAST(:date AS TIMESTAMP) IS NULL) OR ca.calendar_id IS NOT NULL)
            """,
            nativeQuery = true)
    Page<EventEntity> findByAll(Long companyId, Date date, Pageable pageable);
}
