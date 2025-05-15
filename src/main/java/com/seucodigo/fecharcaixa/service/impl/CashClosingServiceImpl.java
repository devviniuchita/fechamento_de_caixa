package com.seucodigo.fecharcaixa.service.impl;

import com.seucodigo.fecharcaixa.dto.CashClosingDTO;
import com.seucodigo.fecharcaixa.exception.ResourceNotFoundException;
import com.seucodigo.fecharcaixa.model.CashClosing;
import com.seucodigo.fecharcaixa.model.User;
import com.seucodigo.fecharcaixa.repository.CashClosingRepository;
import com.seucodigo.fecharcaixa.service.CashClosingService;
import com.seucodigo.fecharcaixa.service.GoogleDriveService;
import com.seucodigo.fecharcaixa.service.UserService;
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
            throw new ResourceNotFoundException("Cash closing not found with id: " + id);
        }
        cashClosingRepository.deleteById(id);
    }

    @Override
    public CashClosing getCashClosingById(String id) {
        return cashClosingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cash closing not found with id: " + id));
    }

    @Override
    public List<CashClosing> getCashClosingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return cashClosingRepository.findByDateBetweenOrderByDateDesc(startDate, endDate);
    }

    @Override
    public List<CashClosing> getCashClosingsByDate(LocalDate date) {
        return cashClosingRepository.findByDateOrderByCreatedAtDesc(date);
    }

    @Override
    public List<CashClosing> getCashClosingsByResponsible(String responsibleId) {
        return cashClosingRepository.findByResponsibleIdOrderByDateDesc(responsibleId);
    }

    @Override
    @Transactional
    public void uploadReceipt(String id, MultipartFile file) {
        try {
            CashClosing cashClosing = getCashClosingById(id);
            String fileId = googleDriveService.uploadFile(file, "receipts/" + id);
            cashClosing.setReceiptImageUrl(fileId);
            cashClosingRepository.save(cashClosing);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload receipt", e);
        }
    }

    @Override
    public byte[] generateDailyReport(LocalDate date) {
        List<CashClosing> closings = getCashClosingsByDate(date);
        return generateExcelReport(closings, "Daily Report - " + date);
    }

    @Override
    public byte[] generateWeeklyReport(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        List<CashClosing> closings = getCashClosingsByDateRange(startDate, endDate);
        return generateExcelReport(closings, "Weekly Report - " + startDate);
    }

    @Override
    public byte[] generateMonthlyReport(LocalDate month) {
        LocalDate startDate = month.withDayOfMonth(1);
        LocalDate endDate = month.withDayOfMonth(month.lengthOfMonth());
        List<CashClosing> closings = getCashClosingsByDateRange(startDate, endDate);
        return generateExcelReport(closings, "Monthly Report - " + month.getMonth().toString());
    }

    @Override
    @Transactional
    public void backupToGoogleDrive(String cashClosingId) {
        CashClosing cashClosing = getCashClosingById(cashClosingId);
        try {
            String backupId = googleDriveService.backupCashClosing(cashClosing);
            cashClosing.setBackupDriveId(backupId);
            cashClosingRepository.save(cashClosing);
        } catch (IOException e) {
            throw new RuntimeException("Failed to backup cash closing", e);
        }
    }

    private void updateCashClosingFromDTO(CashClosing cashClosing, CashClosingDTO dto) {
        cashClosing.setDate(dto.getDate());
        cashClosing.setInitialCash(dto.getInitialCash());
        cashClosing.setSales(dto.getSales());
        cashClosing.setInsertedChange(dto.getInsertedChange());
        
        // Update payment methods
        CashClosing.PaymentMethods payments = new CashClosing.PaymentMethods();
        CashClosingDTO.PaymentMethodsDTO paymentMethodsDTO = dto.getPaymentMethods();
        
        payments.setCash(paymentMethodsDTO.getCash());
        payments.setPix(paymentMethodsDTO.getPix());
        payments.setDeposit(paymentMethodsDTO.getDeposit());
        payments.setWithdrawal(paymentMethodsDTO.getWithdrawal());
        payments.setGiftCard(paymentMethodsDTO.getGiftCard());
        payments.setVisaDebit(paymentMethodsDTO.getVisaDebit());
        payments.setMasterDebit(paymentMethodsDTO.getMasterDebit());
        payments.setEloDebit(paymentMethodsDTO.getEloDebit());
        payments.setVisaCredit(paymentMethodsDTO.getVisaCredit());
        payments.setMasterCredit(paymentMethodsDTO.getMasterCredit());
        payments.setEloCredit(paymentMethodsDTO.getEloCredit());
        payments.setVoucher(paymentMethodsDTO.getVoucher());
        
        cashClosing.setPaymentMethods(payments);
        
        // Update expenses
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

    private byte[] generateExcelReport(List<CashClosing> closings, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Date", "Responsible", "Initial Cash", "Sales", "Total Income", 
                              "Total Assets", "Inconsistency"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // Create data rows
            int rowNum = 1;
            for (CashClosing closing : closings) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(closing.getDate().toString());
                row.createCell(1).setCellValue(closing.getResponsibleName());
                row.createCell(2).setCellValue(closing.getInitialCash().doubleValue());
                row.createCell(3).setCellValue(closing.getSales().doubleValue());
                row.createCell(4).setCellValue(closing.getTotalIncome().doubleValue());
                row.createCell(5).setCellValue(closing.getTotalAssets().doubleValue());
                row.createCell(6).setCellValue(closing.isHasInconsistency() ? "Yes" : "No");
            }
            
            // Auto size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }
} 