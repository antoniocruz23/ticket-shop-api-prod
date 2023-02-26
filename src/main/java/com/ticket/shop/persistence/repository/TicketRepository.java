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

    /**
     * Get Available Tickets by calendar and type
     *
     * @param calendarId   calendar id
     * @param type         type
     * @param totalTickets total of tickets wanted
     * @return list of ticket ids
     */
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


    /**
     * Update ticket with userEntity, WAITING_PAYMENT status and orderId
     *
     * @param userEntity user entity
     * @param ticketIds  ticket ids to be updated
     * @param orderId    order id
     */
    @Transactional
    @Modifying
    @Query(value = """
            update TicketEntity t
            set t.userEntity = :userEntity,
                t.status = 'WAITING_PAYMENT',
                t.paypalOrderId = :orderId
            where t.ticketId in :ticketIds""")
    void updateUserEntityAndStatusByCalendarEntityAndType(UserEntity userEntity, List<Long> ticketIds, String orderId);

    /**
     * Update Ticket to SOLD status by order id
     *
     * @param paypalOrderId order id
     */
    @Transactional
    @Modifying
    @Query("update TicketEntity t set t.status = 'SOLD' where t.paypalOrderId = :paypalOrderId")
    void updateStatusByPaypalOrderId(String paypalOrderId);
}
