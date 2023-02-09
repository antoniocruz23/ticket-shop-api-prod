package com.ticket.shop.service;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.converter.EventConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.address.AddressNotFoundException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.EventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImp implements EventService {

    private static final Logger LOGGER = LogManager.getLogger(CustomerService.class);
    private final EventRepository eventRepository;
    private final AddressService addressService;
    private final AddressRepository addressRepository;
    private final CompanyRepository companyRepository;

    public EventServiceImp(EventRepository eventRepository, AddressService addressService, AddressRepository addressRepository,
                           CompanyRepository companyRepository) {
        this.eventRepository = eventRepository;
        this.addressService = addressService;
        this.addressRepository = addressRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * @see EventService#createEvent(CreateEventDto, Long)
     */
    @Override
    public EventDetailsDto createEvent(CreateEventDto createEventDto, Long companyId) {

        EventEntity eventEntity = EventConverter.fromCreateCompanyDtoToCompanyEntity(createEventDto);

        AddressDetailsDto address = this.addressService.createAddress(createEventDto.getAddress());
        AddressEntity addressEntity = getAddressEntityById(address.getAddressId());
        eventEntity.setAddressEntity(addressEntity);

        CompanyEntity companyEntity = getCompanyEntityById(companyId);
        eventEntity.setCompanyEntity(companyEntity);

        LOGGER.info("Persisting event into database");
        EventEntity createEvent;
        try {
            LOGGER.info("Saving event on database");
            createEvent = this.eventRepository.save(eventEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving event into database {}", eventEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Retrieving created event");
        return EventConverter.fromCompanyEntityToCompanyDetailsDto(createEvent);
    }

    /**
     * Get Address by id
     *
     * @param addressId address id
     * @return {@link CountryEntity}
     */
    private AddressEntity getAddressEntityById(Long addressId) {
        LOGGER.debug("Getting address with id {} from database", addressId);
        return this.addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    LOGGER.error("Address with id {} doesn't exist", addressId);
                    return new AddressNotFoundException(ErrorMessages.ADDRESS_NOT_FOUND);
                });
    }

    /**
     * Get Company by id
     *
     * @param companyId company id
     * @return {@link CompanyEntity}
     */
    private CompanyEntity getCompanyEntityById(Long companyId) {
        LOGGER.debug("Getting company with id {} from database", companyId);
        return this.companyRepository.findById(companyId)
                .orElseThrow(() -> {
                    LOGGER.error("The company with id {} does not exist in database", companyId);
                    return new CompanyNotFoundException(ErrorMessages.COMPANY_NOT_FOUND);
                });
    }
}