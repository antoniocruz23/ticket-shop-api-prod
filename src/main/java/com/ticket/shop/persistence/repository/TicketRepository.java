package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.TicketEntity;
import com.ticket.shop.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository for {@link TicketEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface TicketRepository extends CrudRepository<TicketEntity, Long> {

    @Query(value = """
            select t.ticket_id
            from tickets t
            where t.calendar_id = :calendarId
            and t.type = :type
            and t.user_id is null
            and t.status = 'AVAILABLE'
            limit :totalTickets""",
            nativeQuery = true)
    List<Long> findAvailableByCalendarAndType(Long calendarId, String type, Long totalTickets);


    @Transactional
    @Modifying
    @Query(value = """
            update TicketEntity t
            set t.userEntity = :userEntity,
                t.status = 'WAITING_PAYMENT',
                t.paypalOrderId = :orderId
            where t.ticketId in :ticketIds""")
    void updateUserEntityAndStatusByCalendarEntityAndType(UserEntity userEntity, List<Long> ticketIds, String orderId);

    @Transactional
    @Modifying
    @Query("update TicketEntity t set t.status = 'SOLD' where t.paypalOrderId = :paypalOrderId")
    void updateStatusByPaypalOrderId(String paypalOrderId);
}
