package com.ticket.shop.converter;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateCompanyDto;
import com.ticket.shop.persistence.entity.CompanyEntity;

/**
 * Company converter
 */
public class CompanyConverter {

    public static CompanyEntity fromCreateCompanyDtoToCompanyEntity(CreateCompanyDto createCompanyDto) {
        return CompanyEntity.builder()
                .name(createCompanyDto.getName())
                .email(createCompanyDto.getEmail())
                .website(createCompanyDto.getWebsite())
                .build();
    }

    public static CompanyDetailsDto fromCompanyEntityToCompanyDetailsDto(CompanyEntity companyEntity) {
        return CompanyDetailsDto.builder()
                .companyId(companyEntity.getCompanyId())
                .name(companyEntity.getName())
                .email(companyEntity.getEmail())
                .website(companyEntity.getWebsite())
                .build();
    }
}
