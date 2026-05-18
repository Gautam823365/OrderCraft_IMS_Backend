package com.boot.ordercraft.controller;

import java.io.IOException;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.boot.ordercraft.model.InventoryTransactions;
import com.boot.ordercraft.model.PurchaseOrder;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.service.InventoryTransactionsService;
import com.boot.ordercraft.util.InventoryPdfGenerator;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;


@RestController
@CrossOrigin(origins = "http://localhost:4200") 
@RequestMapping("/api/orders/inventory")
public class InventoryTransactionsController {

    private final InventoryTransactionsService inventoryTransactionsService;

    public InventoryTransactionsController(InventoryTransactionsService inventoryTransactionsService) {
        this.inventoryTransactionsService = inventoryTransactionsService;
    }

    // ✅ Get all inventory transactions
//    @GetMapping("/getalltransactions")
//    public List<InventoryTransactions> getAllTransactions() {
//        return inventoryTransactionsService.getAllTransactions();
//    }

    // ✅ Deliver order and log transaction
    @PostMapping("/deliver/{orderId}")
    public PurchaseOrder deliverOrder(
            @PathVariable Long orderId,
            @RequestBody User performedBy // pass user from frontend
    ) {
        return inventoryTransactionsService.deliverOrder(orderId, performedBy);
    }
    
    @GetMapping("/getalltransactions")
    public List<InventoryTransactions> getAllTransactions(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String performedBy
    ) {
        return inventoryTransactionsService.getFilteredTransactions(productName, transactionType, startDate, endDate, performedBy);
    }
    

    
    @GetMapping("/export/pdf")
    public void exportToPdf(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String performedBy,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=transactions_detailed.pdf");

        List<InventoryTransactions> transactions =
                inventoryTransactionsService.getFilteredTransactions(productName, transactionType, startDate, endDate, performedBy);

        InventoryPdfGenerator.generatePdf(transactions, response);
    }
    
    
    @GetMapping("/export/excel")
    public void exportToExcel(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String performedBy,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");

        List<InventoryTransactions> transactions =
                inventoryTransactionsService.getFilteredTransactions(productName, transactionType, startDate, endDate, performedBy);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            // Header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Product");
            header.createCell(1).setCellValue("Quantity");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Date");
            header.createCell(4).setCellValue("Performed By");

            int rowIdx = 1;
            for (InventoryTransactions tx : transactions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(tx.getProduct().getProductName());
                row.createCell(1).setCellValue(tx.getQuantity());
                row.createCell(2).setCellValue(tx.getTransactionType());
                row.createCell(3).setCellValue(tx.getTransactionDate().toString());
                row.createCell(4).setCellValue(tx.getUserId().getUsername());
            }

            workbook.write(response.getOutputStream());
        }
    }
    

    
}
