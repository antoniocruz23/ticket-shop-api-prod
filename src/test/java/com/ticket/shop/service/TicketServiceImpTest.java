package com.ticket.shop.service;

import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsWhenCreatedDto;
import com.ticket.shop.command.ticket.TotalOfTicketsDto;
import com.ticket.shop.enumerators.TicketStatus;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.calendar.CalendarNotFoundException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.ticket.InvalidTicketTypeException;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.PriceEntity;
import com.ticket.shop.persistence.entity.TicketEntity;
import com.ticket.shop.persistence.repository.CalendarRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
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
public class TicketServiceImpTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CalendarRepository calendarRepository;

    private TicketServiceImp ticketServiceImp;

    private final LocalDateTime refDate = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        this.ticketServiceImp = new TicketServiceImp(this.ticketRepository, this.priceRepository, this.companyRepository, this.calendarRepository);
    }

    /**
     * Create Event Tests
     */
    @Test
    public void testBulkCreateTicketSuccessfully() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.calendarRepository.findByCalendarIdAndCompanyEntity(any(), any())).thenReturn(Optional.ofNullable(getMockedCalendarEntity()));
        when(this.priceRepository.findByTypesAndEventEntity(any(), any())).thenReturn(getMockedPriceEntities());
        when(this.ticketRepository.saveAll(any())).thenReturn(List.of(getMockedTicketEntity()));

        // Method to be tested
        List<TicketDetailsWhenCreatedDto> tickets = this.ticketServiceImp.bulkCreateTicket(1L, getMockedCalendarEntity().getCalendarId(), getMockedCreateTicketDto());

        // Assert Results
        assertEquals(getMockedTicketDetailsWhenCreatedDtoList(), tickets);
    }

    @Test
    public void testBulkCreateTicketFailureDueToCompanyNotFound() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CompanyNotFoundException.class,
                () -> this.ticketServiceImp.bulkCreateTicket(1L, getMockedCalendarEntity().getCalendarId(), getMockedCreateTicketDto()));
    }

    @Test
    public void testBulkCreateTicketFailureDueToCalendarNotFound() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.calendarRepository.findByCalendarIdAndCompanyEntity(any(),any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CalendarNotFoundException.class,
                () -> this.ticketServiceImp.bulkCreateTicket(1L, getMockedCalendarEntity().getCalendarId(), getMockedCreateTicketDto()));
    }

    @Test
    public void testBulkCreateTicketFailureDueToInvalidTicketType() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.calendarRepository.findByCalendarIdAndCompanyEntity(any(), any())).thenReturn(Optional.ofNullable(getMockedCalendarEntity()));
        when(this.priceRepository.findByTypesAndEventEntity(any(), any())).thenReturn(List.of());

        // Assert exception
        assertThrows(InvalidTicketTypeException.class,
                () -> this.ticketServiceImp.bulkCreateTicket(1L, getMockedCalendarEntity().getCalendarId(), getMockedCreateTicketDto()));
    }

    @Test
    public void testBulkCreateTicketFailureDueToDatabaseConnectionFailure() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.calendarRepository.findByCalendarIdAndCompanyEntity(any(), any())).thenReturn(Optional.ofNullable(getMockedCalendarEntity()));
        when(this.priceRepository.findByTypesAndEventEntity(any(), any())).thenReturn(getMockedPriceEntities());
        when(this.ticketRepository.saveAll(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.ticketServiceImp.bulkCreateTicket(1L, getMockedCalendarEntity().getCalendarId(), getMockedCreateTicketDto()));
    }

    /**
     * Get Total Of Tickets By Calendar id tests
     */
    @Test
    public void testGetTotalOfTicketsByCalendarIdSuccessfully() {
        // Mock data
        when(this.ticketRepository.findByCalendarId(any())).thenReturn(List.of(getMockedTicketEntity()));

        // Method to be tested
        TotalOfTicketsDto totalOfTicketsDto = this.ticketServiceImp.getTotalOfTicketsByCalendarId(getMockedCalendarEntity().getCalendarId());

        // Assert
        assertNotNull(totalOfTicketsDto);
        assertEquals(getMockedTotalOfTicketsDto(), totalOfTicketsDto);
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
                .companyEntity(getMockedCompanyEntity())
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
                .amount(10L)
                .build());
    }

    private List<TicketDetailsWhenCreatedDto> getMockedTicketDetailsWhenCreatedDtoList() {
        return List.of(TicketDetailsWhenCreatedDto.builder()
                        .amountOfTickets(1L)
                        .price(30.0)
                        .type(TicketType.VIP)
                        .build());
    }

    private List<PriceEntity> getMockedPriceEntities() {
        return List.of(PriceEntity.builder()
                .priceId(2L)
                .type(TicketType.VIP)
                .price(30.0)
                .build());
    }

    private CompanyEntity getMockedCompanyEntity() {
        return CompanyEntity.builder()
                .companyId(1L)
                .build();
    }

    private TotalOfTicketsDto getMockedTotalOfTicketsDto() {
        return TotalOfTicketsDto.builder()
                .totalOfTickets(1)
                .totalByTypeStatus(Map.of(TicketType.VIP, Map.of(TicketStatus.AVAILABLE, 1L)))
                .build();
    }

}

