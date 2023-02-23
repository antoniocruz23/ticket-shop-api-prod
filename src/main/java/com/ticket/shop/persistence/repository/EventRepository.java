package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.EventEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

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
}
