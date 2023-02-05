package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.CompanyEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for {@link CompanyEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface CompanyRepository extends CrudRepository<CompanyEntity, Long> {

    /**
     * Get company by name
     *
     * @param name name
     * @return Optional of {@link CompanyEntity}
     */
    Optional<CompanyEntity> findByName(String name);

    /**
     * Get company by email
     *
     * @param email email
     * @return Optional of {@link CompanyEntity}
     */
    Optional<CompanyEntity> findByEmail(String email);

    /**
     * Get company by website
     *
     * @param website website
     * @return Optional of {@link CompanyEntity}
     */
    Optional<CompanyEntity> findByWebsite(String website);
}
