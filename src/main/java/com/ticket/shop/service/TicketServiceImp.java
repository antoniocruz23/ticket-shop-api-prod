package com.ticket.shop.service;

import com.ticket.shop.command.ticket.CreateTicketDto;
import com.ticket.shop.command.ticket.TicketDetailsDto;
import com.ticket.shop.converter.TicketConverter;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.persistence.entity.CalendarEntity;
import com.ticket.shop.persistence.entity.TicketEntity;
import com.ticket.shop.persistence.entity.TicketPriceEntity;
import com.ticket.shop.persistence.repository.TicketPriceRepository;
import com.ticket.shop.persistence.repository.TicketRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TicketServiceImp implements TicketService {

    private static final Logger LOGGER = LogManager.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final TicketPriceRepository ticketPriceRepository;

    public TicketServiceImp(TicketRepository ticketRepository, TicketPriceRepository ticketPriceRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketPriceRepository = ticketPriceRepository;
    }

    /**
     * @see TicketService#bulkCreateTicket(List, CalendarEntity)
     */
    @Override
    public List<TicketDetailsDto> bulkCreateTicket(List<CreateTicketDto> createTicketDto, CalendarEntity calendar) {
        LOGGER.debug("Creating tickets - {}", createTicketDto);
        List<TicketEntity> ticketEntities = TicketConverter.fromListOfCreateTicketDtoToListOfTicketEntity(createTicketDto, calendar);

        LOGGER.info("Persisting tickets into database");
        Iterable<TicketEntity> createdTicketsIterable;
        try {
            LOGGER.info("Saving tickets on database");
            createdTicketsIterable = this.ticketRepository.saveAll(ticketEntities);

        } catch (Exception e) {
            LOGGER.error("Failed while saving tickets into database {}", ticketEntities, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        List<TicketType> ticketTypes = createTicketDto.stream().map(CreateTicketDto::getType).distinct().toList();
        List<TicketPriceEntity> ticketPrices = getTicketPriceEntityByType(ticketTypes, calendar.getEventEntity().getEventId());

        LOGGER.debug("Retrieving created tickets");
        List<TicketEntity> tickets = StreamSupport.stream(createdTicketsIterable.spliterator(), false).collect(Collectors.toList());
        return TicketConverter.fromListOfTicketEntityToListOfTicketDetailsDto(tickets, ticketPrices);
    }


    /**
     * Get Ticket Price by types and event id
     *
     * @param ticketTypes ticket type
     * @param eventId     event id
     * @return {@link List<TicketPriceEntity>}
     */
    private List<TicketPriceEntity> getTicketPriceEntityByType(List<TicketType> ticketTypes, Long eventId) {
        LOGGER.debug("Getting ticket prices with types {} from database", ticketTypes);
        return this.ticketPriceRepository.findByValuesAndEventId(ticketTypes, eventId);
    }
}
