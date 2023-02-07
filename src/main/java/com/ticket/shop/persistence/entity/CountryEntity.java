package com.ticket.shop.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Country entity
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "countries")
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long countryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2)
    private String isoCode2;

    @Column(nullable = false, length = 3)
    private String isoCode3;

    @Column(nullable = false, length = 4)
    private String phoneCode;

    @Column(nullable = false, length = 4)
    private String currency;

    @Column(nullable = false, length = 4)
    private String language;

    @OneToMany(mappedBy = "countryEntity")
    private List<UserEntity> users;

    @OneToMany(mappedBy = "countryEntity")
    private List<AddressEntity> addresses;
}
