package com.ticket.shop.service;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.address.AddressNotFoundException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.CountryRepository;
import com.ticket.shop.persistence.repository.EventRepository;
import com.ticket.shop.persistence.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

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
    private PriceRepository ticketPriceRepository;

    private EventServiceImp eventServiceImp;

    @BeforeEach
    void setUp() {
        AddressServiceImp addressServiceImp = new AddressServiceImp(this.addressRepository, this.countryRepository);
        PriceServiceImp ticketPriceServiceImp = new PriceServiceImp(this.ticketPriceRepository);
        this.eventServiceImp = new EventServiceImp(this.eventRepository, addressServiceImp, this.addressRepository, this.companyRepository, ticketPriceServiceImp);
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
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(),getMockedCompanyEntity().getCompanyId()));
    }

    @Test
    public void testCreateEventFailureDueToDatabaseConnectionFailureOnSavingAddress() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(),getMockedCompanyEntity().getCompanyId()));
    }

    @Test
    public void testCreateEventFailureDueToAddressNotFound() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());
        when(this.addressRepository.findById(any())).thenReturn(Optional.empty());

        // Assert exception
        assertThrows(AddressNotFoundException.class,
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(),getMockedCompanyEntity().getCompanyId()));
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
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(),getMockedCompanyEntity().getCompanyId()));
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
                () -> this.eventServiceImp.createEvent(getMockedCreateEventDto(),getMockedCompanyEntity().getCompanyId()));
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
                .build();
    }

    private EventDetailsDto getMockedEventDetailsDto() {
        return EventDetailsDto.builder()
                .eventId(getMockedEventEntity().getEventId())
                .name(getMockedEventEntity().getName())
                .description(getMockedEventEntity().getDescription())
                .address(getMockedAddressDetailsDto())
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
}
