package com.ticket.shop.service;

import com.ticket.shop.command.Paginated;
import com.ticket.shop.command.country.CountryDetailsDto;
import com.ticket.shop.converter.CountryConverter;
import com.ticket.shop.error.ErrorMessages;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.exception.country.CountryNotFoundException;
import com.ticket.shop.persistence.entity.CountryEntity;
import com.ticket.shop.persistence.repository.CountryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link CountryService} implementation
 */
@Service
public class CountryServiceImp implements CountryService {

    private static final Logger LOGGER = LogManager.getLogger(CountryService.class);
    private final CountryRepository countryRepository;

    public CountryServiceImp(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    /**
     * @see CountryService#getCountryById(Long)
     */
    @Override
    public CountryDetailsDto getCountryById(Long countryId) {
        CountryEntity countryEntity = getCountryEntityById(countryId);
        return CountryConverter.fromCountryEntityToCountryDetailsDto(countryEntity);
    }

    /**
     * @see CountryService#getCountryList(int, int)
     */
    @Override
    public Paginated<CountryDetailsDto> getCountryList(int page, int size) {
        LOGGER.debug("Getting countries page {} from database", page);
        Page<CountryEntity> countryEntities;
        try {
            countryEntities = this.countryRepository.findAll(PageRequest.of(page, size));

        } catch (Exception e) {
            LOGGER.error("Failed at getting countries page {} from database", page, e);
            throw new DatabaseCommunicationException(ErrorMessages.DATABASE_COMMUNICATION_ERROR, e);
        }

        LOGGER.debug("Converting country list to CountryDetailsDto");
        List<CountryDetailsDto> countryDetailsDtoList = new ArrayList<>();
        for (CountryEntity countryEntity : countryEntities) {
            countryDetailsDtoList.add(CountryConverter.fromCountryEntityToCountryDetailsDto(countryEntity));
        }

        return new Paginated<>(
                countryDetailsDtoList,
                page,
                countryDetailsDtoList.size(),
                countryEntities.getTotalPages(),
                countryEntities.getTotalElements());
    }

    /**
     * Get Country by id
     *
     * @param countryId country id
     * @return {@link CountryEntity}
     */
    private CountryEntity getCountryEntityById(Long countryId) {
        LOGGER.debug("Getting country with id {} from database", countryId);
        return this.countryRepository.findById(countryId)
                .orElseThrow(() -> {
                    LOGGER.error("Country with id {} doesn't exist", countryId);
                    return new CountryNotFoundException(ErrorMessages.COUNTRY_NOT_FOUND);
                });
    }
}
