package com.ticket.shop.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Address entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(nullable = false)
    private String line1;

    @Column()
    private String line2;

    @Column()
    private String line3;

    @Column(length = 30)
    private String mobileNumber;

    @Column(nullable = false, length = 30)
    private String postCode;

    @Column(nullable = false, length = 30)
    private String city;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity countryEntity;

    @ManyToOne
    @JoinTable(name = "users_addresses",
            joinColumns = @JoinColumn(name = "address_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private UserEntity userEntity;

    @OneToOne(mappedBy = "addressEntity")
    @PrimaryKeyJoinColumn
    private CompanyEntity companyEntity;

    @OneToOne(mappedBy = "addressEntity")
    @PrimaryKeyJoinColumn
    private EventEntity eventEntity;
}
