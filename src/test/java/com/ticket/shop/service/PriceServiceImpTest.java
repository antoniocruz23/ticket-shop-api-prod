package com.ticket.shop.service;

import com.ticket.shop.command.price.CreatePriceDto;
import com.ticket.shop.command.price.PriceDetailsDto;
import com.ticket.shop.enumerators.TicketType;
import com.ticket.shop.exception.DatabaseCommunicationException;
import com.ticket.shop.persistence.entity.EventEntity;
import com.ticket.shop.persistence.entity.PriceEntity;
import com.ticket.shop.persistence.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PriceServiceImpTest {

    @Mock
    private PriceRepository priceRepository;

    private PriceServiceImp priceServiceImp;

    @BeforeEach
    void setUp() {
        this.priceServiceImp = new PriceServiceImp(this.priceRepository);
    }

    /**
     * Create Price Tests
     */
    @Test
    public void testCreatePriceSuccessfully() {
        // Mock data
        when(this.priceRepository.saveAll(any())).thenReturn(List.of(getMockedPriceEntity()));

        // Method to be tested
        List<PriceDetailsDto> prices = this.priceServiceImp.bulkCreatePrice(getMockedCreatePriceDtoList(), getMockedEventEntity());

        // Assert result
        assertEquals(getMockedPriceDetailsDtoList(), prices);
    }

    @Test
    public void testCreatePriceFailureDueToDatabaseConnectionFailure() {
        // Mock data
        when(this.priceRepository.saveAll(any())).thenThrow(RuntimeException.class);

        // Assert exception
        assertThrows(DatabaseCommunicationException.class,
                () -> this.priceServiceImp.bulkCreatePrice(getMockedCreatePriceDtoList(), getMockedEventEntity()));
    }

    private PriceEntity getMockedPriceEntity() {
        return PriceEntity.builder()
                .priceId(1L)
                .type(TicketType.VIP)
                .price(10.0)
                .build();
    }

    private List<CreatePriceDto> getMockedCreatePriceDtoList() {
        return List.of(CreatePriceDto.builder()
                .price(getMockedPriceEntity().getPrice())
                .type(getMockedPriceEntity().getType())
                .build());
    }

    private EventEntity getMockedEventEntity() {
        return EventEntity.builder()
                .eventId(2L)
                .build();
    }

    private List<PriceDetailsDto> getMockedPriceDetailsDtoList() {
        return List.of(PriceDetailsDto.builder()
                .priceId(getMockedPriceEntity().getPriceId())
                .price(getMockedPriceEntity().getPrice())
                .type(getMockedPriceEntity().getType())
                .build());
    }
}
