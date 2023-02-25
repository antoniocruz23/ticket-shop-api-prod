package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.worker.CreateWorkerDto;
import com.ticket.shop.command.worker.UpdateWorkerDto;
import com.ticket.shop.command.worker.WorkerDetailsDto;
import com.ticket.shop.exception.auth.RoleInvalidException;
import com.ticket.shop.exception.company.CompanyNotFoundException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
import com.ticket.shop.exception.user.UserNotFoundException;

/**
 * Common interface for worker services, provides methods to manage workers
 */
public interface WorkerService {

    /**
     * Create new worker
     *
     * @param companyId     company id
     * @param createUserDto {@link CreateWorkerDto}
     * @return {@link WorkerDetailsDto} the worker created
     * @throws UserAlreadyExistsException when the user already exists
     * @throws CompanyNotFoundException   when the company isn't found
     * @throws CountryNotFoundException   when the country isn't found
     * @throws RoleInvalidException       when the role provided is an invalid role for workers
     */
    WorkerDetailsDto createWorker(Long companyId, CreateWorkerDto createUserDto);

    /**
     * Get worker by id
     * Only can get a worker from the same company as the logged-in user company
     *
     * @param companyId company id
     * @param workerId  worker id to be got
     * @return {@link WorkerDetailsDto} the worker obtained
     * @throws UserNotFoundException when the user isn't found
     */
    WorkerDetailsDto getWorkerById(Long companyId, Long workerId);

    /**
     * Get workers list by pagination
     *
     * @param companyId company id
     * @param page      page number
     * @param size      page size
     * @return {@link Paginated<WorkerDetailsDto>}
     */
    Paginated<WorkerDetailsDto> getWorkersList(Long companyId, int page, int size);

    /**
     * Update worker
     *
     * @param companyId       company id
     * @param workerId        worker id to be updated
     * @param updateWorkerDto {@link UpdateWorkerDto}
     * @return {@link WorkerDetailsDto} the worker updated
     * @throws UserNotFoundException when the user isn't found
     * @throws CountryNotFoundException when the country isn't found
     */
    WorkerDetailsDto updateWorker(Long companyId, Long workerId, UpdateWorkerDto updateWorkerDto);

    /**
     * Delete worker
     *
     * @param companyId company id
     * @param workerId  worker id to be deleted
     * @throws UserNotFoundException when the user isn't found
     */
    void deleteWorker(Long companyId, Long workerId);
}
