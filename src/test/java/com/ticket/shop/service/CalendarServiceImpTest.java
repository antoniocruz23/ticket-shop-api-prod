package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CalendarDetailsWithTicketsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;
import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsWhenCreatedDto;
import com.ticket.shop.enumerators.TicketStatus;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.calendar.CalendarNotFoundException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.event.EventNotFoundException;
import com.ticket.shop.exception.ticket.InvalidTicketTypeException;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.PriceEntity;
import com.ticket.shop.persistence.entity.TicketEntity;
import com.ticket.shop.persistence.repository.CalendarRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.EventRepository;
import com.ticket.shop.persistence.repository.PriceRepository;
import com.ticket.shop.persistence.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
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

    @Mock
    private CompanyRepository companyRepository;

    private CalendarServiceImp calendarServiceImp;
    private final LocalDateTime refDate = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        TicketServiceImp ticketServiceImp = new TicketServiceImp(this.ticketRepository, this.priceRepository, this.companyRepository, this.calendarRepository);
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
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.calendarRepository.findByCalendarIdAndCompanyEntity(any(), any())).thenReturn(Optional.ofNullable(getMockedCalendarEntity()));
        when(this.priceRepository.findByValuesAndEventEntity(any(), any())).thenReturn(getMockedPriceEntity());
        when(this.ticketRepository.saveAll(any())).thenReturn(List.of(getMockedTicketEntity()));

        // Method to be tested
        CalendarDetailsWithTicketsDto calendar = this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L);

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
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L));
    }

    @Test
    public void testCreateCalendarFailureDueToDatabaseCommunicationException() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.calendarRepository.save(any())).thenThrow(RuntimeException.class);

        // assert
        assertThrows(DatabaseCommunicationException.class,
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L));
    }

    @Test
    public void testCreateCalendarFailureDueToCompanyNotFound() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.calendarRepository.save(any())).thenReturn(getMockedCalendarEntity());
        when(this.companyRepository.findById(any())).thenReturn(Optional.empty());

        // assert
        assertThrows(CompanyNotFoundException.class,
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L));
    }

    @Test
    public void testCreateCalendarFailureDueToCalendarNotFound() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.calendarRepository.save(any())).thenReturn(getMockedCalendarEntity());
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.calendarRepository.findByCalendarIdAndCompanyEntity(any(), any())).thenReturn(Optional.empty());

        // assert
        assertThrows(CalendarNotFoundException.class,
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L));
    }

    @Test
    public void testCreateCalendarFailureDueToInvalidTicketType() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.calendarRepository.save(any())).thenReturn(getMockedCalendarEntity());
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.calendarRepository.findByCalendarIdAndCompanyEntity(any(), any())).thenReturn(Optional.ofNullable(getMockedCalendarEntity()));
        when(this.priceRepository.findByValuesAndEventEntity(any(), any())).thenReturn(List.of());

        // assert
        assertThrows(InvalidTicketTypeException.class,
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L));
    }

    @Test
    public void testCreateCalendarFailureDueToDatabaseCommunicationExceptionWhenSavingTickets() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.calendarRepository.save(any())).thenReturn(getMockedCalendarEntity());
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.calendarRepository.findByCalendarIdAndCompanyEntity(any(), any())).thenReturn(Optional.ofNullable(getMockedCalendarEntity()));
        when(this.priceRepository.findByValuesAndEventEntity(any(), any())).thenReturn(getMockedPriceEntity());
        when(this.ticketRepository.saveAll(any())).thenThrow(RuntimeException.class);

        // assert
        assertThrows(DatabaseCommunicationException.class,
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L));
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

    /**
     * Get calendar list tests
     */
    @Test
    public void testGetCalendarListSuccessfully() {
        //Mocks
        when(this.eventRepository.findById(any())).thenReturn(Optional.of(getMockedEventEntity()));
        when(this.calendarRepository.findByEventEntity(any(), any())).thenReturn(getMockedPagedCalendarEntity());

        //Call method
        Paginated<CalendarDetailsDto> calendarList = this.calendarServiceImp.getCalendarListByEventId(getMockedEventEntity().getEventId(), 0, 1);

        //Assert result
        assertNotNull(calendarList);
        assertEquals(getMockedPaginatedCalendarDetailsDto(), calendarList);
    }

    @Test
    public void testGetCalendarListFailureDueToEventNotFoundException() {
        //Mocks
        when(this.eventRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class,
                () -> this.calendarServiceImp.getCalendarListByEventId(getMockedEventEntity().getEventId(), 0, 1));
    }

    @Test
    public void testGetCalendarListFailureDueToDatabaseConnectionFailure() {
        //Mocks
        when(this.eventRepository.findById(any())).thenReturn(Optional.of(getMockedEventEntity()));
        when(this.calendarRepository.findByEventEntity(any(), any())).thenThrow(RuntimeException.class);

        assertThrows(DatabaseCommunicationException.class,
                () -> this.calendarServiceImp.getCalendarListByEventId(getMockedEventEntity().getEventId(), 0, 1));
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
                .companyEntity(getMockedCompanyEntity())
                .build();
    }

    private CreateCalendarDto getMockedCreateCalendarDto() {
        return CreateCalendarDto.builder()
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
                .tickets(getMockedTicketDetailsWhenCreatedDto())
                .build();
    }

    private List<TicketDetailsWhenCreatedDto> getMockedTicketDetailsWhenCreatedDto() {
        return List.of(TicketDetailsWhenCreatedDto.builder()
                .amountOfTickets(1L)
                .type(TicketType.VIP)
                .price(30.0)
                .build());
    }

    private CalendarDetailsDto getMockedCalendarDetailsDto() {
        return CalendarDetailsDto.builder()
                .calendarId(4L)
                .startDate(refDate)
                .endDate(refDate.plusDays(1))
                .eventId(getMockedEventEntity().getEventId())
                .companyId(getMockedCompanyEntity().getCompanyId())
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
                .amount(1L)
                .build();
    }

    private Page<CalendarEntity> getMockedPagedCalendarEntity() {
        List<CalendarEntity> content = List.of(getMockedCalendarEntity());
        Pageable pageable = PageRequest.of(0, 1);

        return new PageImpl<>(content, pageable, 1);
    }

    private Paginated<CalendarDetailsDto> getMockedPaginatedCalendarDetailsDto() {
        List<CalendarDetailsDto> calendarDetailsDtoList = List.of(getMockedCalendarDetailsDto());

        return new Paginated<>(
                calendarDetailsDtoList,
                0,
                calendarDetailsDtoList.size(),
                1,
                1);
    }

    private List<PriceEntity> getMockedPriceEntity() {
        return List.of(PriceEntity.builder()
                .eventEntity(getMockedEventEntity())
                .type(TicketType.VIP)
                .price(30.0)
                .build());
    }
}
