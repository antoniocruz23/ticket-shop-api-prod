package com.ticket.shop.service;

import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CalendarDetailsWithTicketsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;
import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.enumerators.TicketStatus;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.calendar.CalendarNotFoundException;
import com.ticket.shop.exception.event.EventNotFoundException;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.TicketEntity;
import com.ticket.shop.persistence.repository.CalendarRepository;
import com.ticket.shop.persistence.repository.EventRepository;
import com.ticket.shop.persistence.repository.PriceRepository;
import com.ticket.shop.persistence.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CalendarServiceImpTest {

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PriceRepository priceRepository;

    private CalendarServiceImp calendarServiceImp;
    private final LocalDateTime refDate = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        TicketServiceImp ticketServiceImp = new TicketServiceImp(this.ticketRepository, this.priceRepository);
        this.calendarServiceImp = new CalendarServiceImp(this.calendarRepository, this.eventRepository, ticketServiceImp);
    }

    /**
     * Create calendar tests
     */
    @Test
    public void testCreateCalendarSuccessfully() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.calendarRepository.save(any())).thenReturn(getMockedCalendarEntity());
        when(this.ticketRepository.saveAll(any())).thenReturn(List.of(getMockedTicketEntity()));

        // Method to be tested
        CalendarDetailsWithTicketsDto calendar = this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto());

        // Assert
        assertNotNull(calendar);
        assertEquals(getMockedCalendarDetailsWithTicketsDto(), calendar);
    }

    @Test
    public void testCreateCalendarFailureDueToEventNotFound() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.empty());

        // assert
        assertThrows(EventNotFoundException.class,
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto()));
    }

    @Test
    public void testCreateCalendarFailureDueToDatabaseCommunicationException() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.calendarRepository.save(any())).thenThrow(RuntimeException.class);

        // assert
        assertThrows(DatabaseCommunicationException.class,
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto()));
    }

    /**
     * Get calendar by id tests
     */
    @Test
    public void testGetCalendarByIdSuccessfully() {
        // Mock data
        when(this.calendarRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCalendarEntity()));

        // Method to be tested
        CalendarDetailsDto calendar = this.calendarServiceImp.getCalendarById(getMockedCalendarEntity().getCalendarId());

        // Assert
        assertNotNull(calendar);
        assertEquals(getMockedCalendarDetailsDto(), calendar);
    }

    @Test
    public void testGetCalendarByIdFailureDueToEventNotFound() {
        // Mock data
        when(this.eventRepository.findById(any())).thenReturn(Optional.empty());

        // assert
        assertThrows(CalendarNotFoundException.class,
                () -> this.calendarServiceImp.getCalendarById(getMockedCalendarEntity().getCalendarId()));
    }

    private CompanyEntity getMockedCompanyEntity() {
        return CompanyEntity.builder()
                .companyId(1L)
                .name("Company")
                .email("email@a.com")
                .website("website.com")
                .build();
    }

    private EventEntity getMockedEventEntity() {
        return EventEntity.builder()
                .eventId(2L)
                .name("Test")
                .description("aa")
                .companyEntity(getMockedCompanyEntity())
                .build();
    }

    private CalendarEntity getMockedCalendarEntity() {
        return CalendarEntity.builder()
                .calendarId(4L)
                .startDate(refDate)
                .endDate(refDate.plusDays(1))
                .eventEntity(getMockedEventEntity())
                .build();
    }

    private CreateCalendarDto getMockedCreateCalendarDto() {
        return CreateCalendarDto.builder()
                .companyId(getMockedCompanyEntity().getCompanyId())
                .eventId(getMockedEventEntity().getEventId())
                .startDate(refDate)
                .endDate(refDate.plusDays(1))
                .tickets(List.of(getMockedCreateTicketDto()))
                .build();
    }

    private CalendarDetailsWithTicketsDto getMockedCalendarDetailsWithTicketsDto() {
        return CalendarDetailsWithTicketsDto.builder()
                .calendarId(4L)
                .startDate(refDate)
                .endDate(refDate.plusDays(1))
                .tickets(Map.of(TicketType.VIP, 1L))
                .eventId(getMockedEventEntity().getEventId())
                .build();
    }

    private CalendarDetailsDto getMockedCalendarDetailsDto() {
        return CalendarDetailsDto.builder()
                .calendarId(4L)
                .startDate(refDate)
                .endDate(refDate.plusDays(1))
                .eventId(getMockedEventEntity().getEventId())
                .build();
    }

    private TicketEntity getMockedTicketEntity() {
        return TicketEntity.builder()
                .ticketId(2L)
                .type(TicketType.VIP)
                .status(TicketStatus.AVAILABLE)
                .calendarEntity(getMockedCalendarEntity())
                .build();
    }

    private CreateTicketDto getMockedCreateTicketDto() {
        return CreateTicketDto.builder()
                .type(TicketType.VIP)
                .total(1L)
                .build();
    }
}
