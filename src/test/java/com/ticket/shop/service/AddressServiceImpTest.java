package com.ticket.shop.service;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.user.UserNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CountryRepository;
import com.ticket.shop.persistence.repository.UserRepository;
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

    @Mock
    private UserRepository userRepository;

    private AddressServiceImp addressServiceImp;

    @BeforeEach
    public void setUp() {
        this.addressServiceImp = new AddressServiceImp(this.addressRepository, this.countryRepository, this.userRepository);
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

    /**
     * Create user address tests
     */
    @Test
    public void testCreateUserAddressSuccessfully() {
        // Mock
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedUserEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());

        // Method to be tested
        AddressDetailsDto address = this.addressServiceImp.createUserAddress(1L, getMockedCreateAddressDto());

        // Assert
        assertNotNull(address);
        assertEquals(getMockedAddressDetailsDto(), address);
    }

    @Test
    public void testCreateUserAddressFailureDueToCountryNotFound() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert
        assertThrows(CountryNotFoundException.class,
                () -> this.addressServiceImp.createUserAddress(1L, getMockedCreateAddressDto()));
    }

    @Test
    public void testCreateUserAddressFailureDueToUserNotFound() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.findById(any())).thenReturn(Optional.empty());

        // Assert
        assertThrows(UserNotFoundException.class,
                () -> this.addressServiceImp.createUserAddress(1L, getMockedCreateAddressDto()));
    }

    @Test
    public void testCreateUserAddressFailureDueToDatabaseCommunication() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.userRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedUserEntity()));
        when(this.addressRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert
        assertThrows(DatabaseCommunicationException.class,
                () -> this.addressServiceImp.createUserAddress(1L, getMockedCreateAddressDto()));
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

    private UserEntity getMockedUserEntity() {
        return UserEntity.builder()
                .userId(1L)
                .countryEntity(getMockedCountryEntity())
                .build();
    }
}
