package com.ticket.shop.service;

import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsDto;
import com.ticket.shop.enumerators.TicketStatus;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.TicketEntity;
import com.ticket.shop.persistence.entity.TicketPriceEntity;
import com.ticket.shop.persistence.repository.TicketPriceRepository;
import com.ticket.shop.persistence.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TicketServiceImpTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketPriceRepository ticketPriceRepository;

    private TicketServiceImp ticketServiceImp;

    private final LocalDateTime refDate = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        this.ticketServiceImp = new TicketServiceImp(ticketRepository, ticketPriceRepository);
    }

    /**
     * Create Event Tests
     */
    @Test
    public void testCreateEventSuccessfully() {
        // Mock data
        when(this.ticketRepository.saveAll(any())).thenReturn(List.of(getMockedTicketEntity()));
        when(this.ticketPriceRepository.findByValuesAndEventId(any(), any())).thenReturn(getMockedTicketPriceEntities());
        // Method to be tested
        List<TicketDetailsDto> tickets = this.ticketServiceImp.bulkCreateTicket(getMockedCreateTicketDto(), getMockedCalendarEntity());

        // Assert Results
        assertEquals(getMockedTicketDetailsDtoList(), tickets);
    }

    @Test
    public void testCreateEventFailureDueToDatabaseConnectionFailure() {
        // Mock data
        when(this.ticketRepository.saveAll(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.ticketServiceImp.bulkCreateTicket(getMockedCreateTicketDto(), getMockedCalendarEntity()));
    }

    private TicketEntity getMockedTicketEntity() {
        return TicketEntity.builder()
                .ticketId(2L)
                .type(TicketType.VIP)
                .status(TicketStatus.AVAILABLE)
                .calendarEntity(getMockedCalendarEntity())
                .build();
    }

    private CalendarEntity getMockedCalendarEntity() {
        return CalendarEntity.builder()
                .calendarId(1L)
                .startDate(refDate)
                .endDate(refDate)
                .eventEntity(getMockedEventEntity())
                .build();
    }

    private EventEntity getMockedEventEntity() {
        return EventEntity.builder()
                .eventId(2L)
                .name("test")
                .build();
    }

    private List<CreateTicketDto> getMockedCreateTicketDto() {
        return List.of(CreateTicketDto.builder()
                .type(TicketType.VIP)
                .total(10L)
                .build());
    }

    private List<TicketDetailsDto> getMockedTicketDetailsDtoList() {
        return List.of(TicketDetailsDto.builder()
                .ticketId(getMockedTicketEntity().getTicketId())
                .status(getMockedTicketEntity().getStatus())
                .type(getMockedTicketEntity().getType())
                .price(30.0)
                .build());
    }

    private List<TicketPriceEntity> getMockedTicketPriceEntities() {
        return List.of(TicketPriceEntity.builder()
                .ticketPriceId(2L)
                .type(TicketType.VIP)
                .value(30.0)
                .build());
    }

}

