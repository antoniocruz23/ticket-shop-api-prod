package com.ticket.shop.service;

import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;
import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsDto;
import com.ticket.shop.converter.CalendarConverter;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.event.EventNotFoundException;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.repository.CalendarRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.EventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An {@link CalendarService} implementation
 */
@Service
public class CalendarServiceImp implements CalendarService {

    private static final Logger LOGGER = LogManager.getLogger(CalendarService.class);
    private final CalendarRepository calendarRepository;
    private final CompanyRepository companyRepository;
    private final EventRepository eventRepository;
    private final TicketServiceImp ticketService;

    public CalendarServiceImp(CalendarRepository calendarRepository, CompanyRepository companyRepository, EventRepository eventRepository, TicketServiceImp ticketService) {
        this.calendarRepository = calendarRepository;
        this.companyRepository = companyRepository;
        this.eventRepository = eventRepository;
        this.ticketService = ticketService;
    }

    /**
     * @see CalendarService#createCalendar(CreateCalendarDto, Long, Long)
     */
    @Override
    public CalendarDetailsDto createCalendar(CreateCalendarDto createCalendarDto, Long companyId, Long eventId) {

        getCompanyEntityById(companyId);
        EventEntity eventEntity = getEventEntityById(eventId);
        if (!Objects.equals(eventEntity.getCompanyEntity().getCompanyId(), companyId)) {
            throw new TicketShopException(ErrorMessages.ACCESS_DENIED);
        }

        LOGGER.debug("Creating calendar - {}", createCalendarDto);
        CalendarEntity calendarEntity = CalendarConverter.fromCreateCalendarDtoToCalendarEntity(createCalendarDto, eventEntity);

        LOGGER.info("Persisting calendar into database");
        CalendarEntity createdCalendar;
        try {
            LOGGER.info("Saving calendar on database");
            createdCalendar = this.calendarRepository.save(calendarEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while saving calendar into database {}", createCalendarDto, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        Map<TicketType, Long> createdTickets = createTickets(createCalendarDto.getTickets(), createdCalendar);

        LOGGER.debug("Retrieving created calendar");
        CalendarDetailsDto calendarDetailsDto = CalendarConverter.fromCalendarEntityToCalendarDetailsDto(createdCalendar);
        calendarDetailsDto.setTickets(createdTickets);
        return calendarDetailsDto;
    }

    private Map<TicketType, Long> createTickets(List<CreateTicketDto> createTicketDtoList, CalendarEntity calendarEntity) {
        List<TicketDetailsDto> createdTickets = this.ticketService.bulkCreateTicket(createTicketDtoList, calendarEntity);
        return createdTickets.stream().collect(Collectors.groupingBy(TicketDetailsDto::getType, Collectors.summingLong(t -> 1L)));
    }

    /**
     * Get Company by id
     *
     * @param companyId company id
     */
    private void getCompanyEntityById(Long companyId) {
        LOGGER.debug("Getting company with id {} from database", companyId);
        this.companyRepository.findById(companyId)
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
