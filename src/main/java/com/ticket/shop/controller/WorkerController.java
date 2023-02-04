package com.ticket.shop.controller;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.command.worker.CreateWorkerDto;
import com.ticket.shop.command.worker.UpdateWorkerDto;
import com.ticket.shop.command.worker.WorkerDetailsDto;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.TicketShopException;
import com.ticket.shop.persistence.entity.UserEntity;
import com.ticket.shop.service.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

/**
 * REST controller responsible for some operations of {@link UserEntity} related to Workers
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Workers", description = "Worker endpoints")
public class WorkerController {

    private static final Logger LOGGER = LogManager.getLogger(CustomerController.class);
    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    /**
     * Create new worker
     *
     * @param createWorkerDto new worker data
     * @return {@link WorkerDetailsDto} the response entity
     */
    @PostMapping("/companies/{companyId}/workers")
    @PreAuthorize("@authorized.hasRole('ADMIN') || @authorized.hasRole('COMPANY_ADMIN')")
    @Operation(summary = "Registration", description = "Register new worker")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<WorkerDetailsDto> workerRegistration(@Valid @RequestBody CreateWorkerDto createWorkerDto,
                                                               @PathVariable Long companyId) {

        LOGGER.info("Request to create new worker - {}", createWorkerDto);
        WorkerDetailsDto workerDetailsDto;
        try {
            workerDetailsDto = this.workerService.createWorker(createWorkerDto, companyId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to created worker - {}", createWorkerDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Worker created successfully. Retrieving created worker with id {}", workerDetailsDto.getUserId());
        return new ResponseEntity<>(workerDetailsDto, HttpStatus.CREATED);
    }

    /**
     * Get worker by company id and worker id
     *
     * @param companyId company id
     * @param workerId  worker id
     * @return {@link WorkerDetailsDto} the worker wanted and Ok httpStatus
     */
    @GetMapping("/companies/{companyId}/workers/{workerId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || @authorized.hasRole('COMPANY_ADMIN') || @authorized.isUser(#workerId)")
    @Operation(summary = "Get worker by company", description = "Get worker by company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<WorkerDetailsDto> getWorkerById(@PathVariable Long companyId,
                                                          @PathVariable Long workerId) {

        LOGGER.info("Request to get worker with id {}", workerId);
        WorkerDetailsDto workerDetailsDto;
        try {
            workerDetailsDto = this.workerService.getWorkerById(workerId, companyId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get worker with id {}", workerId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieved worker with id {}", workerId);
        return new ResponseEntity<>(workerDetailsDto, OK);
    }

    /**
     * Get workers list by company id
     *
     * @param page      page number
     * @param size      page size
     * @param companyId company id
     * @return {@link Paginated <WorkerDetailsDto>} workers list wanted and Ok httpStatus
     */
    @GetMapping("/companies/{companyId}/workers")
    @PreAuthorize("@authorized.hasRole('ADMIN') || @authorized.hasRole('COMPANY_ADMIN')")
    @Operation(summary = "Get workers from a company by pagination", description = "Get workers from a company by pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<Paginated<WorkerDetailsDto>> getPatientsList(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @PathVariable Long companyId) {

        LOGGER.info("Request to get workers list - page: {}, size: {}", page, size);
        Paginated<WorkerDetailsDto> patientsList;
        try {
            patientsList = this.workerService.getWorkersList(page, size, companyId);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to get workers from company {}", companyId, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Retrieving workers list");
        return new ResponseEntity<>(patientsList, HttpStatus.OK);
    }

    /**
     * Update worker
     *
     * @param companyId       the worker id
     * @param updateWorkerDto data to update
     * @return the response entity
     */
    @PutMapping("/companies/{companyId}/workers/{workerId}")
    @PreAuthorize("@authorized.hasRole('ADMIN') || @authorized.hasRole('COMPANY_ADMIN') || @authorized.isUser(#workerId)")
    @Operation(summary = "Update worker", description = "Update worker")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Operation",
                    content = @Content(schema = @Schema(implementation = CustomerDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = ErrorMessages.USER_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = ErrorMessages.DATABASE_COMMUNICATION_ERROR,
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = ErrorMessages.ACCESS_DENIED,
                    content = @Content(schema = @Schema(implementation = Error.class)))})
    public ResponseEntity<WorkerDetailsDto> updateWorker(@PathVariable Long companyId,
                                                         @PathVariable Long workerId,
                                                         @Valid @RequestBody UpdateWorkerDto updateWorkerDto) {

        LOGGER.info("Request to update worker with id {} - {}", workerId, updateWorkerDto);
        WorkerDetailsDto workerDetailsDto;
        try {
            workerDetailsDto = this.workerService.updateWorker(companyId, workerId, updateWorkerDto);

        } catch (TicketShopException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to update worker with id {} - {}", workerId, updateWorkerDto, e);
            throw new TicketShopException(ErrorMessages.OPERATION_FAILED, e);
        }

        LOGGER.info("Worker with id {} updated successfully. Retrieving updated worker", workerId);
        return new ResponseEntity<>(workerDetailsDto, HttpStatus.OK);
    }
}
