package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.event.CreateEventDto;
import com.ticket.shop.command.event.EventDetailsDto;
import com.ticket.shop.command.event.EventDetailsWithCalendarIdsDto;
import com.ticket.shop.converter.EventConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.address.AddressNotFoundException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.event.EventNotFoundException;
import com.ticket.shop.persistence.entity.AddressEntity;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.repository.AddressRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.EventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An {@link EventService} implementation
 */
@Service
public class EventServiceImp implements EventService {

    private static final Logger LOGGER = LogManager.getLogger(EventService.class);
    private final EventRepository eventRepository;
    private final AddressRepository addressRepository;
    private final CompanyRepository companyRepository;
    private final AddressServiceImp addressService;
    private final PriceServiceImp priceServiceImp;

    public EventServiceImp(EventRepository eventRepository, AddressServiceImp addressService, AddressRepository addressRepository,
                           CompanyRepository companyRepository, PriceServiceImp priceServiceImp) {
        this.eventRepository = eventRepository;
        this.addressService = addressService;
        this.addressRepository = addressRepository;
        this.companyRepository = companyRepository;
        this.priceServiceImp = priceServiceImp;
    }

    /**
     * @see EventService#createEvent(CreateEventDto, Long)
     */
    @Transactional
    @Override
    public EventDetailsDto createEvent(CreateEventDto createEventDto, Long companyId) {

        EventEntity eventEntity = EventConverter.fromCreateEventDtoToEventEntity(createEventDto);

        AddressDetailsDto address = this.addressService.createAddress(createEventDto.getAddress());
        AddressEntity addressEntity = getAddressEntityById(address.getAddressId());
        eventEntity.setAddressEntity(addressEntity);

        CompanyEntity companyEntity = getCompanyEntityById(companyId);
        eventEntity.setCompanyEntity(companyEntity);

        LOGGER.info("Persisting event into database");
        EventEntity createdEvent;
        try {
            LOGGER.info("Saving event on database");
            createdEvent = this.eventRepository.save(eventEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving event into database {}", eventEntity, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        this.priceServiceImp.bulkCreatePrices(createEventDto.getPrices(), createdEvent);

        LOGGER.debug("Retrieving created event");
        return EventConverter.fromEventEntityToEventDetailsDto(createdEvent);
    }

    /**
     * @see EventService#getEventById(Long)
     */
    @Override
    public EventDetailsWithCalendarIdsDto getEventById(Long eventId) {
        EventEntity eventEntity = getEventEntityById(eventId);
        List<Long> calendarIds = eventEntity.getCalendars().stream().map(CalendarEntity::getCalendarId).toList();

        return EventConverter.fromEventEntityToEventDetailsWithCalendarIdsDto(eventEntity, calendarIds);
    }

    /**
     * @see EventService#getEventList(int, int, Long, Date)
     */
    @Override
    public Paginated<EventDetailsDto> getEventList(int page, int size, Long companyId, Date date) {
        LOGGER.debug("Getting all events from database");
        Page<EventEntity> eventList;
        try {
            eventList = this.eventRepository.findByAll(companyId, date, PageRequest.of(page, size));

        } catch (Exception e) {
            LOGGER.error("Failed at getting events page from database", e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Converting event list to EventDetailsDto");
        List<EventDetailsDto> eventListResponse = new ArrayList<>();
        for (EventEntity event : eventList) {
            eventListResponse.add(EventConverter.fromEventEntityToEventDetailsDto(event));
        }

        return new Paginated<>(
                eventListResponse,
                page,
                eventListResponse.size(),
                eventList.getTotalPages(),
                eventList.getTotalElements());
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

    /**
     * Get Event by id
     *
     * @param eventId event id
     * @return {@link EventEntity}
     */
    private EventEntity getEventEntityById(Long eventId) {
        LOGGER.debug("Getting event with id {} from database", eventId);
        return this.eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    LOGGER.error("The event with id {} does not exist in database", eventId);
                    return new EventNotFoundException(ErrorMessages.EVENT_NOT_FOUND);
                });
    }
}
