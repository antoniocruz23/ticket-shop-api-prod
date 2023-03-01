package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.CountryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link CountryEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface CountryRepository extends CrudRepository<CountryEntity, Long> {

    /**
     * Get countries with pagination
     *
     * @param pageable pageable
     * @return {@link Page<CountryEntity>}
     */
    Page<CountryEntity> findAll(Pageable pageable);
}
