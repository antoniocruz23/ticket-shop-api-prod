package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.command.event.EventDetailsWithCalendarIdsDto;
import com.ticket.shop.command.event.UpdateEventDto;
import com.ticket.shop.command.price.CreatePriceDto;
import com.ticket.shop.command.price.PriceDetailsDto;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.address.AddressNotFoundException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.event.EventNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.PriceEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.CountryRepository;
import com.ticket.shop.persistence.repository.EventRepository;
import com.ticket.shop.persistence.repository.PriceRepository;
import com.ticket.shop.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventServiceImpTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private UserRepository userRepository;

    private EventServiceImp eventServiceImp;

    @BeforeEach
    void setUp() {
        AddressServiceImp addressServiceImp = new AddressServiceImp(this.addressRepository, this.countryRepository, this.userRepository);
        PriceServiceImp priceServiceImp = new PriceServiceImp(this.priceRepository);
        this.eventServiceImp = new EventServiceImp(this.eventRepository, addressServiceImp, this.addressRepository, this.companyRepository, priceServiceImp);
    }

    /**
     * Create Event Tests
     */
    @Test
    public void testCreateEventSuccessfully() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());
        when(this.addressRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedAddressEntity()));
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.eventRepository.save(any())).thenReturn(getMockedEventEntity());
        when(this.priceRepository.saveAll(any())).thenReturn(List.of(getMockedPriceEntity()));

        // Method to be tested
        EventDetailsDto event = this.eventServiceImp.createEvent(getMockedCreateEventDto(), getMockedCompanyEntity().getCompanyId());

        // Assert result
        assertNotNull(event);
        assertEquals(getMockedEventDetailsDto(), event);
    }

    @Test
    public void testCreateEventFailureDueToCountryNotFound() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CountryNotFoundException.class,
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(), getMockedCompanyEntity().getCompanyId()));
    }

    @Test
    public void testCreateEventFailureDueToDatabaseConnectionFailureOnSavingAddress() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(), getMockedCompanyEntity().getCompanyId()));
    }

    @Test
    public void testCreateEventFailureDueToAddressNotFound() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());
        when(this.addressRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(AddressNotFoundException.class,
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(), getMockedCompanyEntity().getCompanyId()));
    }

    @Test
    public void testCreateEventFailureDueToCompanyNotFound() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());
        when(this.addressRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedAddressEntity()));
        when(this.companyRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(CompanyNotFoundException.class,
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(), getMockedCompanyEntity().getCompanyId()));
    }

    @Test
    public void testCreateEventFailureDueDatabaseConnectionFailureOnSavingEvent() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());
        when(this.addressRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedAddressEntity()));
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.eventRepository.save(any())).thenReturn(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(), getMockedCompanyEntity().getCompanyId()));
    }

    @Test
    public void testCreateEventFailureDueDatabaseConnectionFailureOnSavingPrices() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());
        when(this.addressRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedAddressEntity()));
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.eventRepository.save(any())).thenReturn(getMockedEventEntity());
        when(this.priceRepository.saveAll(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(), getMockedCompanyEntity().getCompanyId()));
    }

    /**
     * Get event by id
     */
    @Test
    public void testGetEventByIdSuccessfully() {
        // Mock data
        when(this.eventRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));

        // Method to be tested
        EventDetailsWithCalendarIdsDto calendar = this.eventServiceImp.getEventById(getMockedEventEntity().getEventId());

        // Assert
        assertNotNull(calendar);
        assertEquals(getMockedEventDetailsWithCalendarIdsDto(), calendar);
    }

    @Test
    public void testGetEventByIdFailureDueToEventNotFound() {
        // Mock data
        when(this.eventRepository.findById(any())).thenReturn(Optional.empty());

        // assert
        assertThrows(EventNotFoundException.class,
                () -> this.eventServiceImp.getEventById(getMockedEventEntity().getEventId()));
    }

    /**
     * Get event list tests
     */
    @Test
    public void testGetEventListSuccessfully() {
        //Mocks
        when(this.eventRepository.findByAll(any(), any(), any())).thenReturn(getMockedPagedEventEntity());

        //Call method
        Paginated<EventDetailsDto> eventList = this.eventServiceImp.getEventList(0, 1, null, null);

        //Assert result
        assertNotNull(eventList);
        assertEquals(getMockedPaginatedEventDetailsDto(), eventList);
    }

    @Test
    public void testGetEventListFailureDueToDatabaseConnectionFailure() {
        //Mocks
        when(this.eventRepository.findByAll(any(), any(), any())).thenThrow(RuntimeException.class);

        assertThrows(DatabaseCommunicationException.class,
                () -> this.eventServiceImp.getEventList(0, 1, null, null));
    }

    /**
     * Update event tests
     */
    @Test
    public void testUpdateEventSuccessfully() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.eventRepository.save(any())).thenReturn(getMockedEventEntity());

        // Method to be tested
        EventDetailsDto event = this.eventServiceImp.updateEvent(2L, 1L, getMockedUpdateEventDto());

        // Assert
        assertNotNull(event);
        assertEquals(getMockedEventDetailsDto(), event);
    }

    @Test
    public void testUpdateEventFailureDueToEventNotFound() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.empty());

        // Assert
        assertThrows(EventNotFoundException.class,
                () -> this.eventServiceImp.updateEvent(2L, 1L, getMockedUpdateEventDto()));
    }

    @Test
    public void testUpdateEventFailureDueToCountryNotFound() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert
        assertThrows(CountryNotFoundException.class,
                () -> this.eventServiceImp.updateEvent(2L, 1L, getMockedUpdateEventDto()));
    }

    @Test
    public void testUpdateEventFailureDueToDatabaseCommunication() {
        // Mock data
        when(this.eventRepository.findByCompanyIdAndEventId(any(), any())).thenReturn(Optional.ofNullable(getMockedEventEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.eventRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert
        assertThrows(DatabaseCommunicationException.class,
                () -> this.eventServiceImp.updateEvent(2L, 1L, getMockedUpdateEventDto()));
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

    private AddressEntity getMockedAddressEntity() {
        return AddressEntity.builder()
                .addressId(3L)
                .line1("line1")
                .postCode("code")
                .city("city")
                .countryEntity(getMockedCountryEntity())
                .build();
    }

    private CompanyEntity getMockedCompanyEntity() {
        return CompanyEntity.builder()
                .companyId(2L)
                .name("Company")
                .email("email@a.com")
                .website("website.com")
                .build();
    }

    private EventEntity getMockedEventEntity() {
        return EventEntity.builder()
                .eventId(1L)
                .name("Event")
                .description("test")
                .companyEntity(getMockedCompanyEntity())
                .addressEntity(getMockedAddressEntity())
                .calendars(List.of(CalendarEntity.builder().calendarId(1L).build()))
                .prices(List.of(getMockedPriceEntity()))
                .build();
    }

    private CreateAddressDto getMockedCreateAddressDto() {
        return CreateAddressDto.builder()
                .line1(getMockedAddressEntity().getLine1())
                .postCode(getMockedAddressEntity().getPostCode())
                .city(getMockedAddressEntity().getCity())
                .countryId(getMockedCountryEntity().getCountryId())
                .build();
    }

    private CreateEventDto getMockedCreateEventDto() {
        return CreateEventDto.builder()
                .name(getMockedEventEntity().getName())
                .description(getMockedEventEntity().getDescription())
                .address(getMockedCreateAddressDto())
                .prices(getMockedCreatePriceDto())
                .build();
    }

    private EventDetailsDto getMockedEventDetailsDto() {
        return EventDetailsDto.builder()
                .eventId(getMockedEventEntity().getEventId())
                .name(getMockedEventEntity().getName())
                .description(getMockedEventEntity().getDescription())
                .address(getMockedAddressDetailsDto())
                .prices(getMockedPriceDetailsDto())
                .build();
    }

    private AddressDetailsDto getMockedAddressDetailsDto() {
        return AddressDetailsDto.builder()
                .addressId(getMockedAddressEntity().getAddressId())
                .line1(getMockedAddressEntity().getLine1())
                .postCode(getMockedAddressEntity().getPostCode())
                .city(getMockedAddressEntity().getCity())
                .countryId(getMockedCountryEntity().getCountryId())
                .build();
    }

    private PriceEntity getMockedPriceEntity() {
        return PriceEntity.builder()
                .priceId(2L)
                .price(12.1)
                .type(TicketType.VIP)
                .build();
    }

    private List<CreatePriceDto> getMockedCreatePriceDto() {
        return List.of(CreatePriceDto.builder()
                .price(getMockedPriceEntity().getPrice())
                .type(getMockedPriceEntity().getType())
                .build());
    }

    private List<PriceDetailsDto> getMockedPriceDetailsDto() {
        return List.of(PriceDetailsDto.builder()
                .priceId(getMockedPriceEntity().getPriceId())
                .price(getMockedPriceEntity().getPrice())
                .type(getMockedPriceEntity().getType())
                .build());
    }

    private EventDetailsWithCalendarIdsDto getMockedEventDetailsWithCalendarIdsDto() {
        return EventDetailsWithCalendarIdsDto.builder()
                .event(getMockedEventDetailsDto())
                .calendarIds(List.of(1L))
                .build();
    }

    private Page<EventEntity> getMockedPagedEventEntity() {
        List<EventEntity> content = List.of(getMockedEventEntity());
        Pageable pageable = PageRequest.of(0, 1);

        return new PageImpl<>(content, pageable, 1);
    }

    private Paginated<EventDetailsDto> getMockedPaginatedEventDetailsDto() {
        List<EventDetailsDto> eventDetailsDtoList = List.of(getMockedEventDetailsDto());

        return new Paginated<>(
                eventDetailsDtoList,
                0,
                eventDetailsDtoList.size(),
                1,
                1);
    }

    private UpdateEventDto getMockedUpdateEventDto() {
        return UpdateEventDto.builder()
                .name(getMockedEventDetailsDto().getName())
                .description(getMockedEventEntity().getDescription())
                .address(getMockedCreateAddressDto())
                .build();
    }
}
