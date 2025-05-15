package com.seucodigo.fecharcaixa.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CashClosingTest {

    @Test
    void whenCreateCashClosing_thenPropertiesAreSet() {
        CashClosing cashClosing = new CashClosing();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        cashClosing.setId("1");
        cashClosing.setDate(today);
        cashClosing.setCreatedAt(now);
        cashClosing.setResponsibleId("user1");
        cashClosing.setResponsibleName("Test User");
        cashClosing.setInitialCash(BigDecimal.valueOf(100));
        cashClosing.setSales(BigDecimal.valueOf(500));
        cashClosing.setInsertedChange(BigDecimal.valueOf(50));
        cashClosing.setHasInconsistency(false);

        assertEquals("1", cashClosing.getId());
        assertEquals(today, cashClosing.getDate());
        assertEquals(now, cashClosing.getCreatedAt());
        assertEquals("user1", cashClosing.getResponsibleId());
        assertEquals("Test User", cashClosing.getResponsibleName());
        assertEquals(BigDecimal.valueOf(100), cashClosing.getInitialCash());
        assertEquals(BigDecimal.valueOf(500), cashClosing.getSales());
        assertEquals(BigDecimal.valueOf(50), cashClosing.getInsertedChange());
        assertFalse(cashClosing.isHasInconsistency());
    }

    @Test
    void whenCreatePaymentMethods_thenPropertiesAreSet() {
        CashClosing.PaymentMethods payments = new CashClosing.PaymentMethods();
        payments.setCash(BigDecimal.valueOf(200));
        payments.setPix(BigDecimal.valueOf(150));
        payments.setDeposit(BigDecimal.valueOf(100));
        payments.setVisaDebit(BigDecimal.valueOf(50));
        payments.setMasterCredit(BigDecimal.valueOf(75));

        assertEquals(BigDecimal.valueOf(200), payments.getCash());
        assertEquals(BigDecimal.valueOf(150), payments.getPix());
        assertEquals(BigDecimal.valueOf(100), payments.getDeposit());
        assertEquals(BigDecimal.valueOf(50), payments.getVisaDebit());
        assertEquals(BigDecimal.valueOf(75), payments.getMasterCredit());
    }

    @Test
    void whenCreateExpense_thenPropertiesAreSet() {
        CashClosing.Expense expense = new CashClosing.Expense();
        expense.setDescription("Office supplies");
        expense.setAmount(BigDecimal.valueOf(25.50));

        assertEquals("Office supplies", expense.getDescription());
        assertEquals(BigDecimal.valueOf(25.50), expense.getAmount());
    }

    @Test
    void whenAddExpenses_thenListIsUpdated() {
        CashClosing cashClosing = new CashClosing();
        ArrayList<CashClosing.Expense> expenses = new ArrayList<>();
        
        CashClosing.Expense expense1 = new CashClosing.Expense();
        expense1.setDescription("Coffee");
        expense1.setAmount(BigDecimal.valueOf(15.00));
        
        CashClosing.Expense expense2 = new CashClosing.Expense();
        expense2.setDescription("Paper");
        expense2.setAmount(BigDecimal.valueOf(10.00));
        
        expenses.add(expense1);
        expenses.add(expense2);
        
        cashClosing.setExpenses(expenses);
        
        assertEquals(2, cashClosing.getExpenses().size());
        assertEquals("Coffee", cashClosing.getExpenses().get(0).getDescription());
        assertEquals(BigDecimal.valueOf(15.00), cashClosing.getExpenses().get(0).getAmount());
    }
} 