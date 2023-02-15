package com.ticket.shop.command.calendar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Date;

/**
 * CreateCalendarDto used to store calendar info when created
 */
@Data
@Builder
public class CreateCalendarDto {

    @Schema(example = "2020-10-03")
    @NotNull(message = "Must have a date")
    private Date date;

    @Schema(example = "20:00:00")
    @NotNull(message = "Must have a start time")
    private LocalTime startTime;

    @Schema(example = "23:00:00")
    @NotNull(message = "Must have a end time")
    private LocalTime endTime;

//TODO
//    @Valid
//    @NotNull(message = "Must have tickets")
//    private List<TicketDetailsDto> tickets;
}
