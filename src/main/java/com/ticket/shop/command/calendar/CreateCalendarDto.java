package com.ticket.shop.command.calendar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * CreateCalendarDto used to store calendar info when created
 */
@Data
@Builder
public class CreateCalendarDto {

    @Schema(example = "2020-10-04T10:00")
    @NotNull(message = "Must have a start date")
    private LocalDateTime startDate;

    @Schema(example = "2020-10-04T20:00")
    @NotNull(message = "Must have a end date")
    private LocalDateTime endDate;

//TODO
//    @Valid
//    @NotNull(message = "Must have tickets")
//    private List<TicketDetailsDto> tickets;
}
