package com.ticket.shop.persistence.entity;

import com.ticket.shop.enumerators.PriceTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Ticket Price entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticketPrices")
public class TicketPriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketPriceId;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PriceTypes type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id", nullable = false)
    @ToString.Exclude
    private EventEntity eventEntity;

    @OneToMany(mappedBy = "ticketPriceEntity")
    @ToString.Exclude
    private List<TicketEntity> tickets;
}
