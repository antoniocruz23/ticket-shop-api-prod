package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.AddressEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link AddressEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

}
