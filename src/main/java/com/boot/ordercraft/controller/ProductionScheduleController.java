package com.boot.ordercraft.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boot.ordercraft.model.Product;
import com.boot.ordercraft.model.ProductionSchedule;
import com.boot.ordercraft.repository.ProductDemandRepository;
import com.boot.ordercraft.service.ProductService;
import com.boot.ordercraft.service.ProductionScheduleService;

import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductionScheduleController {
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductionScheduleService productionScheduleService;
	
	 @Autowired
	 private ProductDemandRepository productDemandRepo;

//	@PostMapping("/schedule-production/{productId}")
//	public ResponseEntity<ProductionSchedule> scheduleProduction(
//	        @PathVariable Long productId,
//	        @RequestBody ProductionSchedule schedule) {
//
//	    return productService.getProductById(productId)
//	            .map(product -> {
//	                schedule.setPsProductId(product);
//	                ProductionSchedule saved = productionScheduleService.createSchedule(schedule);
//	                return ResponseEntity.ok(saved);
//	            })
//	            .orElse(ResponseEntity.notFound().build());
//	}
	
	 @PostMapping("/schedule-production/{demandId}")
	 public ResponseEntity<ProductionSchedule> scheduleProduction(
	         @PathVariable Long demandId,
	         @RequestBody ProductionSchedule schedule) {

	     return productDemandRepo.findById(demandId)
	             .map(demand -> {
	                 // Set the product from the demand
	                 schedule.setPsProductId(demand.getProduct());

	                 // Create schedule and link it to the single demand
	                 ProductionSchedule saved = productionScheduleService.createScheduleForSingleDemand(schedule, demand);
	                 return ResponseEntity.ok(saved);
	             })
	             .orElse(ResponseEntity.notFound().build());
	 }



	@PostMapping("/deliver-product/{productId}")
	public ResponseEntity<Product> deliverProduct(@PathVariable Long productId) {
	    return productService.deliverProduct(productId)
	            .map(ResponseEntity::ok)
	            .orElse(ResponseEntity.notFound().build());
	}
	
    @GetMapping("/scheduled-products")
    public ResponseEntity<List<ProductionSchedule>> getScheduledProducts() {
        List<ProductionSchedule> schedules = productionScheduleService.getAllScheduledProducts();
        return ResponseEntity.ok(schedules);
    }
    

    @PostMapping("/dispatch/{psId}")
    public ResponseEntity<ProductionSchedule> dispatch(@PathVariable Integer psId) {
        return ResponseEntity.ok(productionScheduleService.dispatchProduct(psId));
    }
    
    @GetMapping("/delivered-products")
    public ResponseEntity<List<ProductionSchedule>> getDeliveredProducts() {
        List<ProductionSchedule> delivered = productionScheduleService.getDeliveredProducts();
        return ResponseEntity.ok(delivered);
    }
    
    @GetMapping("/export-delivered/xls")
    public void exportDeliveredToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=delivered_products.xlsx");

        List<ProductionSchedule> delivered = productionScheduleService.getDeliveredProducts();
        productionScheduleService.exportDeliveredToExcel(delivered, response.getOutputStream());
    }

    @GetMapping("/export-delivered/pdf")
    public void exportDeliveredToPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=delivered_products.pdf");

        List<ProductionSchedule> delivered = productionScheduleService.getDeliveredProducts();
        productionScheduleService.exportDeliveredToPDF(delivered, response.getOutputStream());
    }


}
