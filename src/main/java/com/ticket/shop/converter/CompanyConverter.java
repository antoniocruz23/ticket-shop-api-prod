package com.ticket.shop.converter;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateOrUpdateCompanyDto;
import com.ticket.shop.persistence.entity.CompanyEntity;

/**
 * Company converter
 */
public class CompanyConverter {


    /**
     * From {@link CreateOrUpdateCompanyDto} to {@link CompanyEntity}
     * @param createOrUpdateCompanyDto {@link CreateOrUpdateCompanyDto}
     * @return {@link CompanyEntity}
     */
    public static CompanyEntity fromCreateCompanyDtoToCompanyEntity(CreateOrUpdateCompanyDto createOrUpdateCompanyDto) {
        return CompanyEntity.builder()
                .name(createOrUpdateCompanyDto.getName())
                .email(createOrUpdateCompanyDto.getEmail())
                .website(createOrUpdateCompanyDto.getWebsite())
                .build();
    }

    /**
     * From {@link CompanyEntity} to {@link CompanyDetailsDto}
     * @param companyEntity {@link CompanyEntity}
     * @return {@link CompanyDetailsDto}
     */
    public static CompanyDetailsDto fromCompanyEntityToCompanyDetailsDto(CompanyEntity companyEntity) {
        return CompanyDetailsDto.builder()
                .companyId(companyEntity.getCompanyId())
                .name(companyEntity.getName())
                .email(companyEntity.getEmail())
                .website(companyEntity.getWebsite())
                .address(AddressConverter.fromAddressEntityToAddressDetailsDto(companyEntity.getAddressEntity()))
                .build();
    }
}
