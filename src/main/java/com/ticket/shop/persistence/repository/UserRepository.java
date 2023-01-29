package com.ticket.shop.persistence.repository;

import com.ticket.shop.persistence.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for {@link UserEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    /**
     * Get user by email
     * @param email user email
     * @return Optional of {@link UserEntity}
     */
    Optional<UserEntity> findByEmail(String email);
}
