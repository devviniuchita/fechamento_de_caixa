package com.controle.fechamentocaixa.service;

import com.controle.fechamentocaixa.config.TestConfig;
import com.controle.fechamentocaixa.dto.CashClosingDTO;
import com.controle.fechamentocaixa.exception.ResourceNotFoundException;
import com.controle.fechamentocaixa.model.CashClosing;
import com.controle.fechamentocaixa.repository.CashClosingRepository;
import com.controle.fechamentocaixa.service.impl.CashClosingServiceImpl;
import com.controle.fechamentocaixa.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class CashClosingServiceTest {

    @Mock
    private CashClosingRepository cashClosingRepository;

    @Mock
    private GoogleDriveService googleDriveService;

    @InjectMocks
    private CashClosingServiceImpl cashClosingService;

    private CashClosing testCashClosing;
    private CashClosingDTO testCashClosingDTO;

    @BeforeEach
    void setUp() {
        testCashClosing = TestDataBuilder.createTestCashClosing();
        testCashClosingDTO = TestDataBuilder.createTestCashClosingDTO();
    }

    @Test
    void whenCreateCashClosing_thenReturnSavedCashClosing() {
        // Arrange
        when(cashClosingRepository.save(any(CashClosing.class))).thenReturn(testCashClosing);

        // Act
        CashClosing result = cashClosingService.create(testCashClosingDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testCashClosing.getSaldoInicial(), result.getSaldoInicial());
        verify(cashClosingRepository, times(1)).save(any(CashClosing.class));
    }

    @Test
    void whenFindByIdWithValidId_thenReturnCashClosing() {
        // Arrange
        String id = "valid-id";
        when(cashClosingRepository.findById(id)).thenReturn(Optional.of(testCashClosing));

        // Act
        CashClosing result = cashClosingService.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(testCashClosing.getSaldoInicial(), result.getSaldoInicial());
    }

    @Test
    void whenFindByIdWithInvalidId_thenThrowException() {
        // Arrange
        String id = "invalid-id";
        when(cashClosingRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cashClosingService.findById(id);
        });
    }

    @Test
    void whenFindByPeriod_thenReturnList() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(cashClosingRepository.findByPeriod(start, end))
            .thenReturn(Arrays.asList(testCashClosing));

        // Act
        var result = cashClosingService.findByPeriod(start, end);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testCashClosing.getSaldoInicial(), result.get(0).getSaldoInicial());
    }

    @Test
    void whenValidateBalances_thenNoException() {
        // Arrange
        CashClosingDTO dto = new CashClosingDTO();
        dto.setSaldoInicial(new BigDecimal("1000.00"));
        
        // Act & Assert
        assertDoesNotThrow(() -> {
            cashClosingService.create(dto);
        });
    }

    @Test
    void whenUploadReceipts_thenSuccess() {
        // Arrange
        when(googleDriveService.uploadFile(any())).thenReturn("file-id");
        
        // Act & Assert
        assertDoesNotThrow(() -> {
            cashClosingService.create(testCashClosingDTO);
        });
        verify(googleDriveService, times(1)).uploadFile(any());
    }
} 