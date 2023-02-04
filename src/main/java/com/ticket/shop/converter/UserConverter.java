package com.ticket.shop.converter;

import com.ticket.shop.command.auth.PrincipalDto;
import com.ticket.shop.command.customer.CreateCustomerDto;
import com.ticket.shop.command.customer.CustomerDetailsDto;
import com.ticket.shop.command.worker.CreateWorkerDto;
import com.ticket.shop.command.worker.WorkerDetailsDto;
import com.ticket.shop.persistence.entity.UserEntity;

/**
 * User converter for Worker Service and Customer Service
 */
public class UserConverter {

    /**
     * From {@link CreateCustomerDto} to {@link UserEntity}
     * @param createCustomerDto {@link CreateCustomerDto}
     * @return {@link UserEntity}
     */
    public static UserEntity fromCreateCustomerDtoToUserEntity(CreateCustomerDto createCustomerDto) {
        return UserEntity.builder()
                .firstname(createCustomerDto.getFirstname())
                .lastname(createCustomerDto.getLastname())
                .email(createCustomerDto.getEmail())
                .encryptedPassword(createCustomerDto.getPassword())
                .build();
    }

    /**
     * From {@link CreateWorkerDto} to {@link UserEntity}
     * @param createWorkerDto {@link CreateWorkerDto}
     * @return {@link UserEntity}
     */
    public static UserEntity fromCreateWorkerDtoToUserEntity(CreateWorkerDto createWorkerDto) {
        return UserEntity.builder()
                .firstname(createWorkerDto.getFirstname())
                .lastname(createWorkerDto.getLastname())
                .email(createWorkerDto.getEmail())
                .encryptedPassword(createWorkerDto.getPassword())
                .roles(createWorkerDto.getRoles())
                .build();
    }

    /**
     * From {@link UserEntity} to {@link CustomerDetailsDto}
     * @param userEntity {@link UserEntity}
     * @return {@link CustomerDetailsDto}
     */
    public static CustomerDetailsDto fromUserEntityToCustomerDetailsDto(UserEntity userEntity) {
        return CustomerDetailsDto.builder()
                .userId(userEntity.getUserId())
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .email(userEntity.getEmail())
                .countryId(userEntity.getCountryEntity().getCountryId())
                .build();
    }

    /**
     * From {@link UserEntity} to {@link WorkerDetailsDto}
     * @param userEntity {@link UserEntity}
     * @return {@link WorkerDetailsDto}
     */
    public static WorkerDetailsDto fromUserEntityToWorkerDetailsDto(UserEntity userEntity) {
        return WorkerDetailsDto.builder()
                .userId(userEntity.getUserId())
                .firstname(userEntity.getFirstname())
                .lastname(userEntity.getLastname())
                .email(userEntity.getEmail())
                .companyId(userEntity.getCompanyEntity().getCompanyId())
                .countryId(userEntity.getCountryEntity().getCountryId())
                .build();
    }

    /**
     * Convert from {@link UserEntity} to {@link PrincipalDto}
     * @param userEntity {@link UserEntity}
     * @return {@link PrincipalDto}
     */
    public static PrincipalDto fromUserEntityToPrincipalDto(UserEntity userEntity) {
        return PrincipalDto.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getFirstname() + " " + userEntity.getLastname())
                .email(userEntity.getEmail())
                .roles(userEntity.getRoles())
                .countryId(userEntity.getCountryEntity().getCountryId())
                .companyId(userEntity.getCompanyEntity() != null ? userEntity.getCompanyEntity().getCompanyId() : null)
                .build();
    }
}
