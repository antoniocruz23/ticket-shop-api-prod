package com.ticket.shop.service;

import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.event.EventNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.repository.CalendarRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
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
    private CompanyRepository companyRepository;

    @Mock
    private EventRepository eventRepository;

    private CalendarServiceImp calendarServiceImp;
    private final LocalDateTime refDate = LocalDateTime.now();

    @BeforeEach
    public void setUp() {
        this.calendarServiceImp = new CalendarServiceImp(this.calendarRepository, this.companyRepository, this.eventRepository);
    }

    /**
     * Create calendar tests
     */
    @Test
    public void testCreateCalendarSuccessfully() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.eventRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.calendarRepository.save(any())).thenReturn(getMockedCalendarEntity());

        // Method to be tested
        CalendarDetailsDto calendar = this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L);

        // Assert
        assertNotNull(calendar);
        assertEquals(getMockedCalendarDetailsDto(), calendar);
    }

    @Test
    public void testCreateCalendarFailureDueToCompanyNotFound() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.empty());

        // assert
        assertThrows(CompanyNotFoundException.class,
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L));
    }

    @Test
    public void testCreateCalendarFailureDueToEventNotFound() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.eventRepository.findById(any())).thenReturn(Optional.empty());

        // assert
        assertThrows(EventNotFoundException.class,
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L));
    }

    @Test
    public void testCreateCalendarFailureDueToDatabaseCommunicationException() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.eventRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.calendarRepository.save(any())).thenThrow(RuntimeException.class);

        // assert
        assertThrows(DatabaseCommunicationException.class,
                () -> this.calendarServiceImp.createCalendar(getMockedCreateCalendarDto(), 1L, 2L));
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
                .addressEntity(getMockedAddressEntity())
                .companyEntity(getMockedCompanyEntity())
                .build();
    }

    private AddressEntity getMockedAddressEntity() {
        return AddressEntity.builder()
                .addressId(1L)
                .line1("line1")
                .postCode("code")
                .city("city")
                .countryEntity(getMockedCountryEntity())
                .build();
    }

    private CountryEntity getMockedCountryEntity() {
        return CountryEntity.builder()
                .countryId(1L)
                .name("Portugal")
                .isoCode2("PT")
                .isoCode3("PRT")
                .currency("EUR")
                .language("PT")
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
                .startDate(refDate)
                .endDate(refDate.plusDays(1))
                .build();
    }

    private CalendarDetailsDto getMockedCalendarDetailsDto() {
        return CalendarDetailsDto.builder()
                .calendarId(4L)
                .startDate(refDate)
                .endDate(refDate.plusDays(1))
                .build();
    }
}
