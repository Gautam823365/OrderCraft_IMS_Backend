package com.boot.ordercraft.service;

//ProductionScheduleService.java
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.boot.ordercraft.exception.NoResourceAvailableException;
import com.boot.ordercraft.model.ProductDemand;
import com.boot.ordercraft.model.ProductionResource;
import com.boot.ordercraft.model.ProductionSchedule;
import com.boot.ordercraft.repository.ProductDemandRepository;
import com.boot.ordercraft.repository.ProductionResourceRepository;
import com.boot.ordercraft.repository.ProductionScheduleRepository;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Service
public class ProductionScheduleService {

 @Autowired
 private ProductionScheduleRepository scheduleRepo;
 
 @Autowired
 private ProductionResourceRepository resourceRepo;
 
 @Autowired
 private ProductDemandRepository productDemandRepo;


 
// public ProductionSchedule createSchedule(ProductionSchedule schedule) {
//	    // check product demand quantity
//	    if (schedule.getPsQuantity() == null || schedule.getPsQuantity() <= 0) {
//	        throw new RuntimeException("Invalid product quantity");
//	    }
//
//	    // check if resource available
//	    var resourceOpt = resourceRepo.findFirstByStatus("AVAILABLE");
////	    if (resourceOpt.isEmpty()) {
////	        throw new RuntimeException("All production resources are busy. Try scheduling later.");
////	    }
//	    if (resourceOpt.isEmpty()) {
//	        throw new NoResourceAvailableException("All production resources are busy. Try scheduling later.");
//	    }
//
//
//	    ProductionResource resource = resourceOpt.get();
//	    resource.setStatus("BUSY");
//	    resourceRepo.save(resource);
//
//	    schedule.setPsStatus("SCHEDULED");
//	    schedule.setCompletedQuantity(0);
//	    if(schedule.getQcBufferHours() == null) schedule.setQcBufferHours(2);
//	    schedule.setResource(resource);
//	    
//	    if (schedule.getPsEndDate() != null) {
//	        int extraDays = (int) Math.ceil(schedule.getQcBufferHours() / 24.0);
//	        schedule.setPsDeadline(schedule.getPsEndDate().plusDays(extraDays));
//	    }
//
//	    return scheduleRepo.save(schedule);
//	}
 
 @Transactional
 public ProductionSchedule createScheduleForSingleDemand(ProductionSchedule schedule, ProductDemand demand) {
     if (schedule.getPsQuantity() == null || schedule.getPsQuantity() <= 0) {
         throw new RuntimeException("Invalid product quantity");
     }

     var resourceOpt = resourceRepo.findFirstByStatus("AVAILABLE");
     if (resourceOpt.isEmpty()) {
         throw new NoResourceAvailableException("All production resources are busy. Try scheduling after 24hr.");
     }

     ProductionResource resource = resourceOpt.get();
     resource.setStatus("BUSY");
     resourceRepo.save(resource);

     schedule.setPsStatus("SCHEDULED");
     schedule.setCompletedQuantity(0);
     if (schedule.getQcBufferHours() == null) schedule.setQcBufferHours(2);
     schedule.setResource(resource);

     if (schedule.getPsEndDate() != null) {
         int extraDays = (int) Math.ceil(schedule.getQcBufferHours() / 24.0);
         schedule.setPsDeadline(schedule.getPsEndDate().plusDays(extraDays));
     }

     // Save schedule
     ProductionSchedule savedSchedule = scheduleRepo.save(schedule);

     // ✅ Link schedule to the single demand
     demand.setDemandStatus("SCHEDULED");
     demand.setSchedule(savedSchedule);
     productDemandRepo.save(demand);

     // Link demand back to schedule
     savedSchedule.setDemands(List.of(demand));

     return savedSchedule;
 }



 public List<ProductionSchedule> getAllScheduledProducts() {
     return scheduleRepo.findAll();
 }
 
 // 🔹 Auto status update every minute
 @Scheduled(cron = "0 * * * * *")   // every 1 min
 @Transactional
 public void updateStatuses() {
     LocalDate now = LocalDate.now();

     // Move SCHEDULED → IN_PROGRESS
     var toStart = scheduleRepo.findByPsStartDateLessThanEqualAndPsStatus(now, "SCHEDULED");
     toStart.forEach(ps -> ps.setPsStatus("IN_PROGRESS"));
     scheduleRepo.saveAll(toStart);

     // Move IN_PROGRESS → QUALITY_CHECK
     var toQC = scheduleRepo.findByPsEndDateLessThanEqualAndPsStatus(now, "IN_PROGRESS");
     toQC.forEach(ps -> {
         LocalDate qcDate = ps.getPsEndDate().plusDays(ps.getQcBufferHours() / 24);
         if (!now.isBefore(qcDate)) {
             ps.setPsStatus("QUALITY_CHECK");
         }
     });

     // Move QUALITY_CHECK → READY
//     var toReady = scheduleRepo.findByPsEndDateLessThanEqualAndPsStatus(now.minusDays(0), "QUALITY_CHECK");
//     toReady.forEach(ps -> ps.setPsStatus("READY"));
//     scheduleRepo.saveAll(toReady);
     
  // Move QUALITY_CHECK → READY
     var toReady = scheduleRepo.findByPsEndDateLessThanEqualAndPsStatus(now, "QUALITY_CHECK");
     toReady.forEach(ps -> {
         ps.setPsStatus("READY");
         ps.setCompletedQuantity(ps.getPsQuantity()); // 🔹 mark as fully completed
     });
     scheduleRepo.saveAll(toReady);

     
     var inProgress = scheduleRepo.findByPsStatus("IN_PROGRESS");
     inProgress.forEach(ps -> {
         long totalDays = ps.getPsStartDate().until(ps.getPsEndDate()).getDays() + 1;
         long elapsedDays = ps.getPsStartDate().until(LocalDate.now()).getDays() + 1;
         int progress = (int) Math.min(
             ((double) elapsedDays / totalDays) * ps.getPsQuantity(),
             ps.getPsQuantity()
         );
         ps.setCompletedQuantity(progress);
     });
     scheduleRepo.saveAll(inProgress);
 }

//  Supervisor manually dispatch
// public ProductionSchedule dispatchProduct(Integer psId) {
//     ProductionSchedule ps = scheduleRepo.findById(psId)
//             .orElseThrow(() -> new RuntimeException("Schedule not found"));
//     ps.setPsStatus("DISPATCHED");
//     return scheduleRepo.save(ps);
// }
 
 public ProductionSchedule dispatchProduct(Integer psId) {
	    ProductionSchedule ps = scheduleRepo.findById(psId)
	            .orElseThrow(() -> new RuntimeException("Schedule not found"));

	    ps.setPsStatus("DISPATCHED");

	    // free resource
	    ProductionResource resource = ps.getResource();
	    if (resource != null) {
	        resource.setStatus("AVAILABLE");
	        resourceRepo.save(resource);
	    }

	    return scheduleRepo.save(ps);
	}

 
 public List<ProductionSchedule> getDeliveredProducts() {
	    return scheduleRepo.findByPsStatus("DELIVERED");
	}
 
 public void exportDeliveredToExcel(List<ProductionSchedule> delivered, OutputStream os) throws IOException {
     Workbook workbook = new XSSFWorkbook();
     Sheet sheet = workbook.createSheet("Delivered Products");

     Row header = sheet.createRow(0);
     String[] columns = {"ID", "Product", "Quantity", "Start", "End", "Deadline", "Status"};
     for (int i = 0; i < columns.length; i++) {
         header.createCell(i).setCellValue(columns[i]);
     }

     int rowIdx = 1;
     for (ProductionSchedule ps : delivered) {
         Row row = sheet.createRow(rowIdx++);
         row.createCell(0).setCellValue(ps.getPsId());
         row.createCell(1).setCellValue(ps.getPsProductId().getProductName());
         row.createCell(2).setCellValue(ps.getPsQuantity());
         row.createCell(3).setCellValue(ps.getPsStartDate().toString());
         row.createCell(4).setCellValue(ps.getPsEndDate().toString());
         row.createCell(5).setCellValue(ps.getPsDeadline() != null ? ps.getPsDeadline().toString() : "-");
         row.createCell(6).setCellValue(ps.getPsStatus());
     }

     workbook.write(os);
     workbook.close();
 }

 public void exportDeliveredToPDF(List<ProductionSchedule> delivered, OutputStream os) throws IOException {
     Document document = new Document();
     try {
         PdfWriter.getInstance(document, os);
         document.open();

         document.add(new Paragraph("Delivered Products Report"));
         document.add(new Paragraph("Generated on: " + LocalDate.now().toString()));
         document.add(Chunk.NEWLINE);

         PdfPTable table = new PdfPTable(7);
         Stream.of("ID", "Product", "Quantity", "Start", "End", "Deadline", "Status")
               .forEach(col -> table.addCell(new PdfPCell(new Phrase(col))));

         for (ProductionSchedule ps : delivered) {
             table.addCell(String.valueOf(ps.getPsId()));
             table.addCell(ps.getPsProductId().getProductName());
             table.addCell(String.valueOf(ps.getPsQuantity()));
             table.addCell(ps.getPsStartDate().toString());
             table.addCell(ps.getPsEndDate().toString());
             table.addCell(ps.getPsDeadline() != null ? ps.getPsDeadline().toString() : "-");
             table.addCell(ps.getPsStatus());
         }

         document.add(table);
     } catch (DocumentException e) {
         throw new IOException(e);
     } finally {
         document.close();
     }
 }

}
