package com.ticket.shop.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

/**
 * Company entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companies")
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String website;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", nullable = false)
    private AddressEntity addressEntity;

    @Column()
    private Timestamp createdAt;

    @Column()
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "companyEntity", cascade = CascadeType.ALL)
    private List<UserEntity> users;

    @OneToMany(mappedBy = "companyEntity", cascade = CascadeType.ALL)
    private List<EventEntity> events;

    @OneToMany(mappedBy = "companyEntity", cascade = CascadeType.ALL)
    private List<CalendarEntity> calendars;

    @OneToMany(mappedBy = "companyEntity", cascade = CascadeType.ALL)
    private List<TicketEntity> tickets;

    @OneToMany(mappedBy = "companyEntity")
    private List<PriceEntity> prices;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
