package com.ticket.shop.service;

import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsWhenCreatedDto;
import com.ticket.shop.converter.TicketConverter;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.calendar.CalendarNotFoundException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.ticket.InvalidTicketTypeException;
import com.ticket.shop.exception.ticket.TicketCantBeDeletedException;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.PriceEntity;
import com.ticket.shop.persistence.entity.TicketEntity;
import com.ticket.shop.persistence.repository.CalendarRepository;
import com.ticket.shop.persistence.repository.CompanyRepository;
import com.ticket.shop.persistence.repository.PriceRepository;
import com.ticket.shop.persistence.repository.TicketRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An {@link TicketService} implementation
 */
@Service
public class TicketServiceImp implements TicketService {

    private static final Logger LOGGER = LogManager.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final PriceRepository priceRepository;
    private final CompanyRepository companyRepository;
    private final CalendarRepository calendarRepository;

    public TicketServiceImp(TicketRepository ticketRepository, PriceRepository priceRepository, CompanyRepository companyRepository, CalendarRepository calendarRepository) {
        this.ticketRepository = ticketRepository;
        this.priceRepository = priceRepository;
        this.companyRepository = companyRepository;
        this.calendarRepository = calendarRepository;
    }

    /**
     * @see TicketService#bulkCreateTicket(Long, Long, List)
     */
    @Override
    public List<TicketDetailsWhenCreatedDto> bulkCreateTicket(Long companyId, Long calendarId, List<CreateTicketDto> createTicketDto) {
        CompanyEntity companyEntity = getCompanyById(companyId);
        CalendarEntity calendarEntity = getCalendarByCalendarIdAndCompanyEntity(calendarId, companyEntity);

        List<PriceEntity> prices = getTicketPrices(createTicketDto, calendarEntity.getEventEntity());

        LOGGER.debug("Creating tickets - {}", createTicketDto);
        List<TicketEntity> ticketEntities = TicketConverter.fromListOfCreateTicketDtoToListOfTicketEntity(createTicketDto, calendarEntity);

        LOGGER.info("Persisting tickets into database");
        Iterable<TicketEntity> createdTicketsIterable;
        try {
            LOGGER.info("Saving tickets on database");
            createdTicketsIterable = this.ticketRepository.saveAll(ticketEntities);

        } catch (Exception e) {
            LOGGER.error("Failed while saving tickets into database {}", ticketEntities, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Retrieving created tickets");
        List<TicketEntity> tickets = StreamSupport.stream(createdTicketsIterable.spliterator(), false).collect(Collectors.toList());
        return TicketConverter.fromListOfTicketEntityToListOfTicketDetailsWhenCreatedDto(tickets, prices);
    }

    /**
     * @see TicketService#deleteTicketsByCalendarId(Long, Long)
     */
    @Override
    public void deleteTicketsByCalendarId(Long companyId, Long calendarId) {
        CompanyEntity companyEntity = getCompanyById(companyId);
        CalendarEntity calendarEntity = getCalendarByCalendarIdAndCompanyEntity(calendarId, companyEntity);

        LOGGER.debug("Getting tickets with calendar id {} from database", calendarId);
        if (verifyTicketStatus(calendarEntity)) {
            LOGGER.error("Tickets with calendar id {} can't be deleted due some already been sold", calendarId);
            throw new TicketCantBeDeletedException(ErrorMessages.TICKET_CANT_BE_DELETED);
        }

        LOGGER.debug("Removing tickets with calendar id {} from database", calendarId);
        try {
            this.ticketRepository.deleteByCalendarEntityAndCompanyEntity(calendarEntity, companyEntity);

        } catch (Exception e) {
            LOGGER.error("Failed while deleting tickets with calendar id {} from database", calendarId, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }
    }

    /**
     * Get Prices by types and event id
     *
     * @param ticketTypes ticket type
     * @param eventEntity event entity
     * @return {@link List<PriceEntity>}
     */
    private List<PriceEntity> getTicketPriceEntityByType(List<TicketType> ticketTypes, EventEntity eventEntity) {
        LOGGER.debug("Getting prices with types {} from database", ticketTypes);
        return this.priceRepository.findByValuesAndEventEntity(ticketTypes, eventEntity);
    }

    /**
     * Get company by id
     *
     * @param companyId company id
     */
    private CompanyEntity getCompanyById(Long companyId) {
        return this.companyRepository.findById(companyId)
                .orElseThrow(() -> {
                    LOGGER.error("The company with id {} does not exist in database", companyId);
                    return new CompanyNotFoundException(ErrorMessages.COMPANY_NOT_FOUND);
                });
    }

    /**
     * Get calendar by id and company entity
     *
     * @param calendarId    calendar id
     * @param companyEntity company id
     * @return {@link CalendarEntity}
     */
    private CalendarEntity getCalendarByCalendarIdAndCompanyEntity(Long calendarId, CompanyEntity companyEntity) {
        return this.calendarRepository.findByCalendarIdAndCompanyEntity(calendarId, companyEntity)
                .orElseThrow(() -> {
                    LOGGER.error("The calendar with id {} does not exist in database", calendarId);
                    return new CalendarNotFoundException(ErrorMessages.CALENDAR_NOT_FOUND);
                });
    }

    /**
     * Verify if the ticket to be deleted are associated with a customer
     *
     * @param calendarEntity calendar id
     * @return true if it doesn't have a customer associated and false if it has
     */
    private boolean verifyTicketStatus(CalendarEntity calendarEntity) {
        return this.ticketRepository.existsByCalendarEntityAndUserEntityNull(calendarEntity);
    }

    /**
     * Get ticket prices
     * And also verifying if it has all types needed
     *
     * @param createTicketDto {@link List<CreateTicketDto>}
     * @param eventEntity     {@link EventEntity}
     * @return {@link List<PriceEntity>}
     */
    private List<PriceEntity> getTicketPrices(List<CreateTicketDto> createTicketDto, EventEntity eventEntity) {
        List<TicketType> ticketTypes = createTicketDto.stream().map(CreateTicketDto::getType).distinct().toList();
        List<PriceEntity> prices = getTicketPriceEntityByType(ticketTypes, eventEntity);
        List<TicketType> priceTypes = prices.stream().map(PriceEntity::getType).distinct().toList();

        if (!ticketTypes.equals(priceTypes)) {
            LOGGER.error("The given ticket types are invalid or dont have all the types needed");
            throw new InvalidTicketTypeException(ErrorMessages.INVALID_TICKET_TYPE);
        }
        return prices;
    }
}
