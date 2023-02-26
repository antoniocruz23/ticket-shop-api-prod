package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.calendar.CalendarDetailsDto;
import com.ticket.shop.command.calendar.CalendarDetailsWithTicketsDto;
import com.ticket.shop.command.calendar.CreateCalendarDto;
import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsDto;
import com.ticket.shop.converter.CalendarConverter;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.calendar.CalendarNotFoundException;
import com.ticket.shop.exception.event.EventNotFoundException;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.repository.CalendarRepository;
import com.ticket.shop.persistence.repository.EventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An {@link CalendarService} implementation
 */
@Service
public class CalendarServiceImp implements CalendarService {

    private static final Logger LOGGER = LogManager.getLogger(CalendarService.class);
    private final CalendarRepository calendarRepository;
    private final EventRepository eventRepository;
    private final TicketServiceImp ticketService;

    public CalendarServiceImp(CalendarRepository calendarRepository, EventRepository eventRepository, TicketServiceImp ticketService) {
        this.calendarRepository = calendarRepository;
        this.eventRepository = eventRepository;
        this.ticketService = ticketService;
    }

    /**
     * @see CalendarService#createCalendar(CreateCalendarDto, Long)
     */
    @Override
    public CalendarDetailsWithTicketsDto createCalendar(CreateCalendarDto createCalendarDto, Long eventId) {

        EventEntity eventEntity = getEventEntityByCompanyIdAndEventId(createCalendarDto.getCompanyId(), eventId);

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
        CalendarDetailsWithTicketsDto calendarDetailsDto = CalendarConverter.fromCalendarEntityToCalendarDetailsWithTicketsDto(createdCalendar);
        calendarDetailsDto.setTickets(createdTickets);
        return calendarDetailsDto;
    }

    /**
     * @see CalendarService#getCalendarById(Long)
     */
    @Override
    public CalendarDetailsDto getCalendarById(Long calendarId) {
        CalendarEntity calendarEntity = getCalendarEntityById(calendarId);
        return CalendarConverter.fromCalendarEntityToCalendarDetailsDto(calendarEntity);
    }

    /**
     * @see CalendarService#getCalendarListByEventId(Long, int, int)
     */
    @Override
    public Paginated<CalendarDetailsDto> getCalendarListByEventId(Long eventId, int page, int size) {
        EventEntity eventEntity = getEventEntityById(eventId);

        LOGGER.debug("Getting all calendars from event id {} from database", eventId);
        Page<CalendarEntity> calendarList;
        try {
            calendarList = this.calendarRepository.findByEventEntity(eventEntity, PageRequest.of(page, size));

        } catch (Exception e) {
            LOGGER.error("Failed at getting calendars page from database", e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Converting calendars list to CalendarDetailsDto");
        List<CalendarDetailsDto> workerListResponse = new ArrayList<>();
        for (CalendarEntity calendar : calendarList) {
            workerListResponse.add(CalendarConverter.fromCalendarEntityToCalendarDetailsDto(calendar));
        }

        return new Paginated<>(
                workerListResponse,
                page,
                workerListResponse.size(),
                calendarList.getTotalPages(),
                calendarList.getTotalElements());
    }

    private Map<TicketType, Long> createTickets(List<CreateTicketDto> createTicketDtoList, CalendarEntity calendarEntity) {
        List<TicketDetailsDto> createdTickets = this.ticketService.bulkCreateTicket(createTicketDtoList, calendarEntity);
        return createdTickets.stream().collect(Collectors.groupingBy(TicketDetailsDto::getType, Collectors.summingLong(t -> 1L)));
    }

    /**
     * Get Event by company id and event id
     *
     * @param companyId company id
     * @param eventId   event id
     * @return {@link EventEntity}
     */
    private EventEntity getEventEntityByCompanyIdAndEventId(Long companyId, Long eventId) {
        LOGGER.debug("Getting event with id {} and company id {} from database", eventId, companyId);
        return this.eventRepository.findByCompanyIdAndEventId(companyId, eventId)
                .orElseThrow(() -> {
                    LOGGER.error("The event with id {} or company id {} does not exist in database", eventId, companyId);
                    return new EventNotFoundException(ErrorMessages.EVENT_NOT_FOUND);
                });
    }

    /**
     * Get calendar id
     *
     * @param calendarId calendar id
     * @return {@link CalendarEntity}
     */
    private CalendarEntity getCalendarEntityById(Long calendarId) {
        LOGGER.debug("Getting calendar with id {} from database", calendarId);
        return this.calendarRepository.findById(calendarId).orElseThrow(() -> {
            LOGGER.error("The calendar with id {} does not exist in database", calendarId);
            return new CalendarNotFoundException(ErrorMessages.CALENDAR_NOT_FOUND);
        });
    }

    /**
     * Get event id
     *
     * @param eventId event id
     * @return {@link EventEntity}
     */
    private EventEntity getEventEntityById(Long eventId) {
        LOGGER.debug("Getting event with id {} from database", eventId);
        return this.eventRepository.findById(eventId).orElseThrow(() -> {
            LOGGER.error("The event with id {} does not exist in database", eventId);
            return new EventNotFoundException(ErrorMessages.EVENT_NOT_FOUND);
        });
    }
}
