package com.ticket.shop.controller;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.country.CountryDetailsDto;
import com.ticket.shop.error.Error;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.service.CountryServiceImp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

/**
 * REST controller responsible for country operations
 */
@RestController
@RequestMapping("/api/countries")
@Tag(name = "Countries", description = "Country endpoints")
public class CountryController {

    private static final Logger LOGGER = LogManager.getLogger(CountryController.class);
    private final CountryServiceImp countryServiceImp;

    public CountryController(CountryServiceImp countryServiceImp) {
        this.countryServiceImp = countryServiceImp;
    }

    /**
     * Get country by id
     *
     * @param countryId country id
     * @return {@link CountryDetailsDto} the country wanted and Ok httpStatus
     */
    @GetMapping("/{countryId}")
    @Operation(summary = "Get country by id", description = "Get country by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CountryDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.COUNTRY_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<CountryDetailsDto> getCountryById(@PathVariable Long countryId) {

        LOGGER.info("Request to get country with id {}", countryId);
        CountryDetailsDto countryDetailsDto;
        try {
            countryDetailsDto = this.countryServiceImp.getCountryById(countryId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get country with id {}", countryId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved country with id {}", countryId);
        return new ResponseEntity<>(countryDetailsDto, OK);
    }

    /**
     * Get all countries with pagination
     *
     * @param page page number
     * @param size page size
     * @return {@link Paginated<CountryDetailsDto>}
     */
    @GetMapping()
    @Operation(summary = "Get countries with pagination", description = "Get countries with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CountryDetailsDto.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<Paginated<CountryDetailsDto>> getAllCountries(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {

        LOGGER.info("Request to get country list - page: {}, size: {}", page, size);
        Paginated<CountryDetailsDto> countryDetailsDtoList;
        try {
            countryDetailsDtoList = this.countryServiceImp.getCountryList(page, size);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get country list - page: {}, size: {}", page, size, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved country list");
        return new ResponseEntity<>(countryDetailsDtoList, OK);
    }
}
