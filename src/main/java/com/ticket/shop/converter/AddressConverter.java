package com.ticket.shop.converter;

import com.ticket.shop.command.address.AddressDetailsDto;
import com.ticket.shop.command.address.CreateAddressDto;
import com.ticket.shop.persistence.entity.AddressEntity;

/**
 * Address converter
 */
public class AddressConverter {

    /**
     * From {@link CreateAddressDto} to {@link AddressEntity}
     *
     * @param createAddressDto {@link CreateAddressDto}
     * @return {@link AddressEntity}
     */
    public static AddressEntity fromCreateAddressDtoToAddressEntity(CreateAddressDto createAddressDto) {
        return AddressEntity.builder()
                .line1(createAddressDto.getLine1())
                .line2(createAddressDto.getLine2())
                .line3(createAddressDto.getLine3())
                .mobileNumber(createAddressDto.getMobileNumber())
                .postCode(createAddressDto.getPostCode())
                .city(createAddressDto.getCity())
                .build();
    }

    /**
     * From {@link AddressEntity} to {@link AddressDetailsDto}
     *
     * @param addressEntity {@link AddressEntity}
     * @return {@link AddressDetailsDto}
     */
    public static AddressDetailsDto fromAddressEntityToAddressDetailsDto(AddressEntity addressEntity) {
        return AddressDetailsDto.builder()
                .addressId(addressEntity.getAddressId())
                .line1(addressEntity.getLine1())
                .line2(addressEntity.getLine2())
                .line3(addressEntity.getLine3())
                .mobileNumber(addressEntity.getMobileNumber())
                .postCode(addressEntity.getPostCode())
                .city(addressEntity.getCity())
                .countryId(addressEntity.getCountryEntity().getCountryId())
                .build();
    }
}
