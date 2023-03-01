package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateOrUpdateCompanyDto;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.address.AddressNotFoundException;
import com.ticket.shop.exception.company.CompanyAlreadyExistsException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.CountryRepository;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CompanyServiceImpTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private UserRepository userRepository;

    private CompanyServiceImp companyServiceImp;

    private final static Long COMPANY_ID = 245L;
    private final static String NAME = "Company";
    private final static String EMAIL = "company@email.com";
    private final static String WEBSITE = "website.com";

    @BeforeEach
    public void setUp() {
        AddressServiceImp addressService = new AddressServiceImp(this.addressRepository, this.countryRepository, this.userRepository);
        this.companyServiceImp = new CompanyServiceImp(this.companyRepository, this.addressRepository, addressService);
    }

    /**
     * Create company tests
     */
    @Test
    public void testCreateCompanySuccessfully() {
        // Mock data
        when(this.companyRepository.findByName(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByWebsite(any())).thenReturn(Optional.empty());

        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());
        when(this.addressRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedAddressEntity()));

        when(this.companyRepository.save(any())).thenReturn(getMockedCompanyEntity());

        // Method to be tested
        CompanyDetailsDto company = this.companyServiceImp.createCompany(getMockedCreateOrUpdateCompanyDto());

        // Assert
        assertNotNull(company);
        assertEquals(getMockedCompanyDetailsDto(), company);
    }

    @Test
    public void testCreateCompanyFailureDueToCountryNotFound() {
        // Mock data
        when(this.companyRepository.findByName(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByWebsite(any())).thenReturn(Optional.empty());
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // assert
        assertThrows(CountryNotFoundException.class,
                () -> this.companyServiceImp.createCompany(getMockedCreateOrUpdateCompanyDto()));
    }

    @Test
    public void testCreateCompanyFailureDueToAddressNotFound() {
        // Mock data
        when(this.companyRepository.findByName(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByWebsite(any())).thenReturn(Optional.empty());

        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());
        when(this.addressRepository.findById(any())).thenReturn(Optional.empty());

        // assert
        assertThrows(AddressNotFoundException.class,
                () -> this.companyServiceImp.createCompany(getMockedCreateOrUpdateCompanyDto()));
    }

    @Test
    public void testCreateCompanyFailureDueToCompanyAlreadyExistsNameValidation() {
        // Mock data
        when(this.companyRepository.findByName(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));

        // assert
        try {
            this.companyServiceImp.createCompany(getMockedCreateOrUpdateCompanyDto());

        } catch (Exception e) {
            assertEquals(CompanyAlreadyExistsException.class, e.getClass());
            assertEquals(ErrorMessages.NAME_ALREADY_EXISTS, e.getMessage());
        }
    }

    @Test
    public void testCreateCompanyFailureDueToCompanyAlreadyExistsEmailValidation() {
        // Mock data
        when(this.companyRepository.findByName(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByEmail(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));

        // assert
        try {
            this.companyServiceImp.createCompany(getMockedCreateOrUpdateCompanyDto());

        } catch (Exception e) {
            assertEquals(CompanyAlreadyExistsException.class, e.getClass());
            assertEquals(ErrorMessages.EMAIL_ALREADY_EXISTS, e.getMessage());
        }
    }

    @Test
    public void testCreateCompanyFailureDueToCompanyAlreadyExistsWebsiteValidation() {
        // Mock data
        when(this.companyRepository.findByName(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByWebsite(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));

        // assert
        try {
            this.companyServiceImp.createCompany(getMockedCreateOrUpdateCompanyDto());

        } catch (Exception e) {
            assertEquals(CompanyAlreadyExistsException.class, e.getClass());
            assertEquals(ErrorMessages.WEBSITE_ALREADY_EXISTS, e.getMessage());
        }
    }

    @Test
    public void testCreateCompanyFailureDueToDatabaseCommunicationException() {
        // Mock data
        when(this.companyRepository.findByName(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(this.companyRepository.findByWebsite(any())).thenReturn(Optional.empty());

        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.addressRepository.save(any())).thenReturn(getMockedAddressEntity());
        when(this.addressRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedAddressEntity()));

        when(this.companyRepository.save(any())).thenThrow(RuntimeException.class);

        // assert
        assertThrows(DatabaseCommunicationException.class,
                () -> this.companyServiceImp.createCompany(getMockedCreateOrUpdateCompanyDto()));
    }

    /**
     * Get company by id tests
     */
    @Test
    public void testGetCompanyByIdSuccessfully() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));

        // Method to be tested
        CompanyDetailsDto company = this.companyServiceImp.getCompanyById(COMPANY_ID);

        // Assert
        assertNotNull(company);
        assertEquals(getMockedCompanyDetailsDto(), company);
    }

    @Test
    public void testGetCompanyByIdFailureDueToCompanyNotFound() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.empty());

        // Assert
        assertThrows(CompanyNotFoundException.class,
                () -> this.companyServiceImp.getCompanyById(COMPANY_ID));
    }

    /**
     * Update company tests
     */
    @Test
    public void testUpdateCompanySuccessfully() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.companyRepository.save(any())).thenReturn(getMockedCompanyEntity());

        // Method to be tested
        CompanyDetailsDto company = this.companyServiceImp.updateCompany(COMPANY_ID, getMockedCreateOrUpdateCompanyDto());

        // Assert
        assertNotNull(company);
        assertEquals(getMockedCompanyDetailsDto(), company);
    }

    @Test
    public void testUpdateCompanyFailureDueToCompanyNotFound() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.empty());

        // Assert
        assertThrows(CompanyNotFoundException.class,
                () -> this.companyServiceImp.updateCompany(COMPANY_ID, getMockedCreateOrUpdateCompanyDto()));
    }

    @Test
    public void testUpdateCompanyFailureDueToCountryNotFound() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.empty());

        // Assert
        assertThrows(CountryNotFoundException.class,
                () -> this.companyServiceImp.updateCompany(COMPANY_ID, getMockedCreateOrUpdateCompanyDto()));
    }

    @Test
    public void testUpdateCompanyFailureDueToDatabaseCommunication() {
        // Mock data
        when(this.companyRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCompanyEntity()));
        when(this.countryRepository.findById(any())).thenReturn(Optional.ofNullable(getMockedCountryEntity()));
        when(this.companyRepository.save(any())).thenThrow(RuntimeException.class);

        // Assert
        assertThrows(DatabaseCommunicationException.class,
                () -> this.companyServiceImp.updateCompany(COMPANY_ID, getMockedCreateOrUpdateCompanyDto()));
    }

    /**
     * Delete company tests
     */
    @Test
    public void testDeleteCompanySuccessfully() {
        // Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.of(getMockedCompanyEntity()));

        // Call method to be tested
        this.companyServiceImp.deleteCompany(COMPANY_ID);

        verify(this.companyRepository).delete(any());
    }

    @Test
    public void testDeleteCompanyFailureDueToCompanyNotFound() {
        // Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class,
                () -> this.companyServiceImp.deleteCompany(COMPANY_ID));
    }

    @Test
    public void testDeleteCompanyFailureDueToDatabaseConnectionFailure() {
        // Mocks
        when(this.companyRepository.findById(any())).thenReturn(Optional.of(getMockedCompanyEntity()));
        doThrow(RuntimeException.class).when(this.companyRepository).delete(any());

        assertThrows(DatabaseCommunicationException.class,
                () -> this.companyServiceImp.deleteCompany(COMPANY_ID));
    }

    /**
     * Get company list tests
     */
    @Test
    public void testGetCompanyListSuccessfully() {
        //Mocks
        when(this.companyRepository.findAll(any())).thenReturn(getMockedPagedCompanyEntity());

        //Call method
        Paginated<CompanyDetailsDto> workerDetailsDto = this.companyServiceImp.getCompanyList(0, 1);

        //Assert result
        assertNotNull(workerDetailsDto);
        assertEquals(getMockedPaginatedCompanyDetailsDto(), workerDetailsDto);
    }

    @Test
    public void testGetCompanyListFailureDueToDatabaseConnectionFailure() {
        //Mocks
        when(this.companyRepository.findAll(any())).thenThrow(RuntimeException.class);

        assertThrows(DatabaseCommunicationException.class,
                () -> this.companyServiceImp.getCompanyList(0, 1));
    }

    private CreateOrUpdateCompanyDto getMockedCreateOrUpdateCompanyDto() {
        return CreateOrUpdateCompanyDto.builder()
                .name(NAME)
                .email(EMAIL)
                .website(WEBSITE)
                .address(getMockedCreateAddressDto())
                .build();
    }

    private CompanyEntity getMockedCompanyEntity() {
        return CompanyEntity.builder()
                .companyId(COMPANY_ID)
                .name(NAME)
                .email(EMAIL)
                .website(WEBSITE)
                .addressEntity(getMockedAddressEntity())
                .build();
    }

    private CompanyDetailsDto getMockedCompanyDetailsDto() {
        return CompanyDetailsDto.builder()
                .companyId(COMPANY_ID)
                .name(NAME)
                .email(EMAIL)
                .website(WEBSITE)
                .address(getMockedAddressDto())
                .build();
    }

    private CreateAddressDto getMockedCreateAddressDto() {
        return CreateAddressDto.builder()
                .line1("line1")
                .line2("")
                .line3("")
                .mobileNumber("")
                .postCode("code")
                .city("city")
                .countryId(getMockedCountryEntity().getCountryId())
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

    private AddressDetailsDto getMockedAddressDto() {
        return AddressDetailsDto.builder()
                .addressId(1L)
                .line1("line1")
                .line2("")
                .line3("")
                .mobileNumber("")
                .postCode("code")
                .city("city")
                .countryId(getMockedCountryEntity().getCountryId())
                .build();
    }

    private AddressEntity getMockedAddressEntity() {
        return AddressEntity.builder()
                .addressId(1L)
                .line1("line1")
                .line2("")
                .line3("")
                .mobileNumber("")
                .postCode("code")
                .city("city")
                .countryEntity(getMockedCountryEntity())
                .build();
    }

    private Page<CompanyEntity> getMockedPagedCompanyEntity() {
        List<CompanyEntity> content = List.of(getMockedCompanyEntity());
        Pageable pageable = PageRequest.of(0, 1);

        return new PageImpl<>(content, pageable, 1);
    }

    private Paginated<CompanyDetailsDto> getMockedPaginatedCompanyDetailsDto() {
        List<CompanyDetailsDto> companyDetailsDto = List.of(getMockedCompanyDetailsDto());

        return new Paginated<>(
                companyDetailsDto,
                0,
                companyDetailsDto.size(),
                1,
                1);
    }
}
