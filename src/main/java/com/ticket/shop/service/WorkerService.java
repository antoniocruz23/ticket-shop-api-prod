package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.worker.CreateWorkerDto;
import com.ticket.shop.command.worker.UpdateWorkerDto;
import com.ticket.shop.command.worker.WorkerDetailsDto;
import com.ticket.shop.exception.user.UserAlreadyExistsException;
import com.ticket.shop.exception.user.UserNotFoundException;

/**
 * Common interface for worker services, provides methods to manage workers
 */
public interface WorkerService {

    /**
     * Create new worker
     *
     * @param createUserDto {@link CreateWorkerDto}
     * @param companyId        company id
     * @return {@link WorkerDetailsDto} the worker created
     * @throws UserAlreadyExistsException when the user already exists
     */
    WorkerDetailsDto createWorker(CreateWorkerDto createUserDto, Long companyId) throws UserAlreadyExistsException;

    /**
     * Get worker by id
     * Only can get a worker from the same company as the userId company
     *
     * @param workerId  worker id to be got
     * @param companyId company id
     * @return {@link WorkerDetailsDto} the worker obtained
     * @throws UserNotFoundException when the user isn't found
     */
    WorkerDetailsDto getWorkerById(Long workerId, Long companyId) throws UserNotFoundException;

    /**
     * Get workers list by pagination
     *
     * @param page      page number
     * @param size      page size
     * @param companyId company id
     * @return {@link Paginated<WorkerDetailsDto>}
     */
    Paginated<WorkerDetailsDto> getWorkersList(int page, int size, Long companyId);

    /**
     * Update worker
     *
     * @param workerId        worker id to be updated
     * @param updateWorkerDto {@link UpdateWorkerDto}
     * @return {@link WorkerDetailsDto} the worker updated
     * @throws UserNotFoundException when the user isn't found
     */
    WorkerDetailsDto updateWorker(Long workerId, UpdateWorkerDto updateWorkerDto) throws UserNotFoundException;
}
