package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.country.CountryDetailsDto;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.repository.CountryRepository;
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
public class CountryServiceImpTest {

    @Mock
    private CountryRepository countryRepository;
    private CountryServiceImp countryServiceImp;

    @BeforeEach
    public void setUp() {
        this.countryServiceImp = new CountryServiceImp(this.countryRepository);
    }

    /**
     * Get country by id tests
     */
    @Test
    public void testGetCountryByIdSuccessfully() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));

        // Method to be tested
        CountryDetailsDto country = this.countryServiceImp.getCountryById(1L);

        // Assert
        assertNotNull(country);
        assertEquals(getMockedCountryDetailsDto(), country);
    }

    @Test
    public void testGetCountryByIdFailureDueToCountryNotFound() {
        // Mock data
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert
        assertThrows(CountryNotFoundException.class,
                () -> this.countryServiceImp.getCountryById(1L));
    }

    /**
     * Get country list tests
     */
    @Test
    public void testGetCountryListSuccessfully() {
        //Mocks
        when(this.countryRepository.findAll(any())).thenReturn(getMockedPagedCountryEntity());

        //Call method
        Paginated<CountryDetailsDto> workerDetailsDto = this.countryServiceImp.getCountryList(0, 1);

        //Assert result
        assertNotNull(workerDetailsDto);
        assertEquals(getMockedPaginatedCountryDetailsDto(), workerDetailsDto);
    }

    @Test
    public void testGetCountryListFailureDueToDatabaseConnectionFailure() {
        //Mocks
        when(this.countryRepository.findAll(any())).thenThrow(RuntimeException.class);

        assertThrows(DatabaseCommunicationException.class,
                () -> this.countryServiceImp.getCountryList(0, 1));
    }

    private CountryEntity getMockedCountryEntity() {
        return CountryEntity.builder()
                .countryId(1L)
                .name("Tuga")
                .isoCode2("PT")
                .isoCode3("PRT")
                .currency("EUR")
                .language("PT")
                .phoneCode("351")
                .build();
    }

    private CountryDetailsDto getMockedCountryDetailsDto() {
        return CountryDetailsDto.builder()
                .countryId(1L)
                .name("Tuga")
                .isoCode2("PT")
                .isoCode3("PRT")
                .currency("EUR")
                .language("PT")
                .phoneCode("351")
                .build();
    }

    private Page<CountryEntity> getMockedPagedCountryEntity() {
        List<CountryEntity> content = List.of(getMockedCountryEntity());
        Pageable pageable = PageRequest.of(0, 1);

        return new PageImpl<>(content, pageable, 1);
    }

    private Paginated<CountryDetailsDto> getMockedPaginatedCountryDetailsDto() {
        List<CountryDetailsDto> countryDetailsDto = List.of(getMockedCountryDetailsDto());

        return new Paginated<>(
                countryDetailsDto,
                0,
                countryDetailsDto.size(),
                1,
                1
        );
    }
}
