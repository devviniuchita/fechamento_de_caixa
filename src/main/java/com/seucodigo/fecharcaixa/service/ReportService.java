package com.seucodigo.fecharcaixa.service;

import com.seucodigo.fecharcaixa.model.CashClosing;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    public byte[] generateDailyReport(List<CashClosing> closings) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Fechamentos Diários");
            
            // Criar cabeçalho
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Data");
            headerRow.createCell(1).setCellValue("Responsável");
            headerRow.createCell(2).setCellValue("Total Vendas");
            headerRow.createCell(3).setCellValue("Total Caixa");
            headerRow.createCell(4).setCellValue("Status");

            // Preencher dados
            int rowNum = 1;
            for (CashClosing closing : closings) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(closing.getDate().toString());
                row.createCell(1).setCellValue(closing.getResponsibleName());
                row.createCell(2).setCellValue(closing.getSales().doubleValue());
                row.createCell(3).setCellValue(closing.getTotalAssets().doubleValue());
                row.createCell(4).setCellValue(closing.isHasInconsistency() ? "Inconsistente" : "OK");
            }

            // Retornar bytes do arquivo
            return workbook.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório", e);
        }
    }
} 