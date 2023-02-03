package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.CompanyEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link CompanyEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface CompanyRepository extends CrudRepository<CompanyEntity, Long> {


}
