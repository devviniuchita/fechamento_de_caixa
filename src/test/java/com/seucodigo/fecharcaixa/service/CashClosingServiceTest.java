package com.seucodigo.fecharcaixa.service;

import com.seucodigo.fecharcaixa.model.CashClosing;
import com.seucodigo.fecharcaixa.repository.CashClosingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CashClosingServiceTest {

    @Mock
    private CashClosingRepository repository;

    @InjectMocks
    private CashClosingService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCashClosing_ShouldCreateNewCashClosing() {
        // Arrange
        CashClosing cashClosing = new CashClosing();
        cashClosing.setDate(LocalDate.now());
        cashClosing.setInitialCash(new BigDecimal("100.00"));
        
        when(repository.save(any(CashClosing.class))).thenReturn(cashClosing);

        // Act
        CashClosing result = service.createCashClosing(cashClosing, "user123");

        // Assert
        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getDate());
        assertEquals(new BigDecimal("100.00"), result.getInitialCash());
        verify(repository, times(1)).save(any(CashClosing.class));
    }

    @Test
    void getCashClosingById_ShouldReturnCashClosing() {
        // Arrange
        String id = "123";
        CashClosing cashClosing = new CashClosing();
        cashClosing.setId(id);
        
        when(repository.findById(id)).thenReturn(Optional.of(cashClosing));

        // Act
        CashClosing result = service.getCashClosingById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(repository, times(1)).findById(id);
    }
} 