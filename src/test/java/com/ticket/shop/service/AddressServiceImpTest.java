package com.ticket.shop.service;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CountryRepository;
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
public class AddressServiceImpTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CountryRepository countryRepository;

    private AddressServiceImp addressServiceImp;

    @BeforeEach
    public void setUp() {
        this.addressServiceImp = new AddressServiceImp(addressRepository, countryRepository);
    }

    /**
     * Create address tests
     */
    @Test
    public void testCreateAddressSuccessfully() {
        // Mock
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());

        // Method to be tested
        AddressDetailsDto address = this.addressServiceImp.createAddress(getMockedCreateAddressDto());

        // Assert
        assertNotNull(address);
        assertEquals(getMockedAddressDetailsDto(), address);
    }

    @Test
    public void testCreateAddressFailureDueToCountryNotFound() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert
        assertThrows(CountryNotFoundException.class,
                () -> this.addressServiceImp.createAddress(getMockedCreateAddressDto()));
    }

    @Test
    public void testCreateAddressFailureDueToDatabaseCommunication() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert
        assertThrows(DatabaseCommunicationException.class,
                () -> this.addressServiceImp.createAddress(getMockedCreateAddressDto()));
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
                .addressId(1L)
                .line1("line1")
                .postCode("code")
                .city("city")
                .countryEntity(getMockedCountryEntity())
                .build();
    }

    private CreateAddressDto getMockedCreateAddressDto() {
        return CreateAddressDto.builder()
                .line1("line1")
                .postCode("code")
                .city("city")
                .countryId(getMockedCountryEntity().getCountryId())
                .build();
    }

    private AddressDetailsDto getMockedAddressDetailsDto() {
        return AddressDetailsDto.builder()
                .addressId(1L)
                .line1("line1")
                .postCode("code")
                .city("city")
                .countryId(getMockedCountryEntity().getCountryId())
                .build();
    }
}
