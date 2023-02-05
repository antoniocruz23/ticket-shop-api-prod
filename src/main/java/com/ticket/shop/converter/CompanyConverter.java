package com.ticket.shop.converter;

import com.ticket.shop.command.company.CompanyDetailsDto;
import com.ticket.shop.command.company.CreateAndUpdateCompanyDto;
import com.ticket.shop.persistence.entity.CompanyEntity;

/**
 * Company converter
 */
public class CompanyConverter {

    public static CompanyEntity fromCreateCompanyDtoToCompanyEntity(CreateAndUpdateCompanyDto createAndUpdateCompanyDto) {
        return CompanyEntity.builder()
                .name(createAndUpdateCompanyDto.getName())
                .email(createAndUpdateCompanyDto.getEmail())
                .website(createAndUpdateCompanyDto.getWebsite())
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
