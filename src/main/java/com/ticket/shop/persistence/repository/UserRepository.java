package com.ticket.shop.persistence.repository;

import com.ticket.shop.enumerators.UserRole;
import com.ticket.shop.persistence.entity.CompanyEntity;
import com.ticket.shop.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.Optional;

/**
 * Repository for {@link UserEntity} persistence operations
 * This interface is implemented by Spring Data JPA
 */
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    /**
     * Get user by email
     *
     * @param email user email
     * @return Optional of {@link UserEntity}
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Get Worker by id and company
     *
     * @param companyId company id
     * @param userId    worker id
     * @return Optional of {@link UserEntity}
     */
    @Query(value = """
            select u.*, r.roles
            from users u
            left join roles r on u.user_id = r.user_id
            where u.user_id = :userId
            and u.company_id = :companyId""",
            nativeQuery = true)
    Optional<UserEntity> findByUserIdAndCompanyId(Long companyId, Long userId);

    /**
     * Get page of workers by user entity
     *
     * @param companyEntity company
     * @param pageable      pageable
     * @return {@link Page<UserEntity>}
     */
    Page<UserEntity> findByCompanyEntity(CompanyEntity companyEntity, Pageable pageable);

    /**
     * Get user list by role
     *
     * @param role     role
     * @param pageable pageable
     * @return {@link Page<UserEntity>}
     */
    Page<UserEntity> findByRolesContains(UserRole role, Pageable pageable);

    /**
     * Get user by reset token and expire token
     *
     * @param token token
     * @param date  {@link Date}
     * @return {@link Optional<UserEntity>}
     */
    Optional<UserEntity> findByResetPasswordTokenAndResetPasswordExpireTokenIsAfter(String token, Date date);

    /**
     * Get user by confirm email token and expire token
     *
     * @param token token
     * @param date  {@link Date}
     * @return {@link Optional<UserEntity>}
     */
    Optional<UserEntity> findByConfirmEmailTokenAndConfirmEmailExpireTokenIsAfter(String token, Date date);
}
