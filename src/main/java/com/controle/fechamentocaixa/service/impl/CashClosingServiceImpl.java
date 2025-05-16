package com.controle.fechamentocaixa.service.impl;

import com.controle.fechamentocaixa.dto.CashClosingDTO;
import com.controle.fechamentocaixa.exception.ResourceNotFoundException;
import com.controle.fechamentocaixa.model.CashClosing;
import com.controle.fechamentocaixa.model.User;
import com.controle.fechamentocaixa.repository.CashClosingRepository;
import com.controle.fechamentocaixa.service.CashClosingService;
import com.controle.fechamentocaixa.service.GoogleDriveService;
import com.controle.fechamentocaixa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashClosingServiceImpl implements CashClosingService {

    private final CashClosingRepository cashClosingRepository;
    private final UserService userService;
    private final GoogleDriveService googleDriveService;

    @Override
    @Transactional
    public CashClosing createCashClosing(CashClosingDTO dto, String userId) {
        User responsible = userService.getUserById(userId);
        
        CashClosing cashClosing = new CashClosing();
        updateCashClosingFromDTO(cashClosing, dto);
        
        cashClosing.setCreatedAt(LocalDateTime.now());
        cashClosing.setResponsibleId(userId);
        cashClosing.setResponsibleName(responsible.getName());
        
        calculateTotals(cashClosing);
        
        return cashClosingRepository.save(cashClosing);
    }

    @Override
    @Transactional
    public CashClosing updateCashClosing(String id, CashClosingDTO dto) {
        CashClosing cashClosing = getCashClosingById(id);
        updateCashClosingFromDTO(cashClosing, dto);
        calculateTotals(cashClosing);
        return cashClosingRepository.save(cashClosing);
    }

    @Override
    @Transactional
    public void deleteCashClosing(String id) {
        if (!cashClosingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fechamento de caixa não encontrado com id: " + id);
        }
        cashClosingRepository.deleteById(id);
    }

    @Override
    public CashClosing getCashClosingById(String id) {
        return cashClosingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fechamento de caixa não encontrado com id: " + id));
    }

    @Override
    public List<CashClosing> getCashClosingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return cashClosingRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public List<CashClosing> getCashClosingsByResponsible(String responsibleId) {
        return cashClosingRepository.findByResponsibleId(responsibleId);
    }

    @Override
    public List<CashClosing> getCashClosingsByResponsibleAndPeriod(String responsibleId, LocalDate startDate, LocalDate endDate) {
        return cashClosingRepository.findByResponsibleIdAndCreatedAtBetween(responsibleId, startDate, endDate);
    }

    @Override
    public String uploadReceiptImage(String cashClosingId, MultipartFile file) {
        try {
            String fileId = googleDriveService.uploadFile(file);
            CashClosing cashClosing = getCashClosingById(cashClosingId);
            cashClosing.setReceiptImageUrl(fileId);
            cashClosingRepository.save(cashClosing);
            return fileId;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload do comprovante", e);
        }
    }

    @Override
    public byte[] generateExcelReport(LocalDate startDate, LocalDate endDate) {
        List<CashClosing> cashClosings = getCashClosingsByDateRange(startDate, endDate);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Fechamentos de Caixa");
            
            // Criar cabeçalho
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Data");
            headerRow.createCell(1).setCellValue("Responsável");
            headerRow.createCell(2).setCellValue("Saldo Inicial");
            headerRow.createCell(3).setCellValue("Vendas");
            headerRow.createCell(4).setCellValue("Total em Caixa");
            headerRow.createCell(5).setCellValue("Inconsistência");
            
            // Preencher dados
            int rowNum = 1;
            for (CashClosing cashClosing : cashClosings) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(cashClosing.getCreatedAt().toString());
                row.createCell(1).setCellValue(cashClosing.getResponsibleName());
                row.createCell(2).setCellValue(cashClosing.getInitialCash().doubleValue());
                row.createCell(3).setCellValue(cashClosing.getSales().doubleValue());
                row.createCell(4).setCellValue(cashClosing.getTotalAssets().doubleValue());
                row.createCell(5).setCellValue(cashClosing.isHasInconsistency() ? "Sim" : "Não");
            }
            
            // Auto-dimensionar colunas
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar relatório Excel", e);
        }
    }

    private void updateCashClosingFromDTO(CashClosing cashClosing, CashClosingDTO dto) {
        cashClosing.setInitialCash(dto.getInitialCash());
        cashClosing.setSales(dto.getSales());
        cashClosing.setInsertedChange(dto.getInsertedChange());
        
        CashClosing.PaymentMethods payments = new CashClosing.PaymentMethods();
        payments.setCash(dto.getPaymentMethods().getCash());
        payments.setPix(dto.getPaymentMethods().getPix());
        payments.setDeposit(dto.getPaymentMethods().getDeposit());
        payments.setGiftCard(dto.getPaymentMethods().getGiftCard());
        payments.setWithdrawal(dto.getPaymentMethods().getWithdrawal());
        payments.setVisaDebit(dto.getPaymentMethods().getVisaDebit());
        payments.setMasterDebit(dto.getPaymentMethods().getMasterDebit());
        payments.setEloDebit(dto.getPaymentMethods().getEloDebit());
        payments.setVisaCredit(dto.getPaymentMethods().getVisaCredit());
        payments.setMasterCredit(dto.getPaymentMethods().getMasterCredit());
        payments.setEloCredit(dto.getPaymentMethods().getEloCredit());
        payments.setVoucher(dto.getPaymentMethods().getVoucher());
        cashClosing.setPaymentMethods(payments);
        
        if (dto.getExpenses() != null) {
            List<CashClosing.Expense> expenses = dto.getExpenses().stream()
                .map(expenseDTO -> {
                    CashClosing.Expense expense = new CashClosing.Expense();
                    expense.setDescription(expenseDTO.getDescription());
                    expense.setAmount(expenseDTO.getAmount());
                    return expense;
                })
                .toList();
            cashClosing.setExpenses(expenses);
        }
    }

    private void calculateTotals(CashClosing cashClosing) {
        // Calculate total income
        BigDecimal totalIncome = cashClosing.getInitialCash()
            .add(cashClosing.getSales())
            .add(cashClosing.getInsertedChange());
        cashClosing.setTotalIncome(totalIncome);
        
        // Calculate total assets
        CashClosing.PaymentMethods payments = cashClosing.getPaymentMethods();
        BigDecimal totalAssets = payments.getCash()
            .add(payments.getPix())
            .add(payments.getDeposit())
            .add(payments.getGiftCard())
            .add(payments.getWithdrawal())
            .add(payments.getVisaDebit())
            .add(payments.getMasterDebit())
            .add(payments.getEloDebit())
            .add(payments.getVisaCredit())
            .add(payments.getMasterCredit())
            .add(payments.getEloCredit())
            .add(payments.getVoucher());
        
        // Add expenses
        BigDecimal totalExpenses = cashClosing.getExpenses().stream()
            .map(CashClosing.Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        cashClosing.setTotalAssets(totalAssets);
        
        // Check for inconsistencies
        cashClosing.setHasInconsistency(!totalIncome.equals(totalAssets.add(totalExpenses)));
    }
} 