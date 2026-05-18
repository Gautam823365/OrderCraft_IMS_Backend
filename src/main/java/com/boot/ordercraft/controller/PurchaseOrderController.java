package com.boot.ordercraft.controller;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boot.ordercraft.model.PurchaseOrder;
import com.boot.ordercraft.service.DownloadPdfService;
import com.boot.ordercraft.service.PurchaseOrderService;
import com.boot.ordercraft.util.Utilities;

@RestController
@RequestMapping("/api/orders")
//@CrossOrigin(origins = "http://localhost:53898")
@CrossOrigin(origins = "http://localhost:4200")
public class PurchaseOrderController {

	private final PurchaseOrderService orderService;
	
	 @Autowired
	    private Utilities securityUtil;
	 
	 @Autowired
	    private DownloadPdfService pdfService;

	public PurchaseOrderController(PurchaseOrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping("/history")
	public List<PurchaseOrder> viewOrderHistory(@RequestParam(required = false) String date,
			@RequestParam(required = false) String status) {

		LocalDate parsedDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date.trim()) : null;

		// ✅ Trim and lowercase the status to match DB handling
		String cleanStatus = (status != null && !status.isBlank()) ? status.trim().toLowerCase() : null;

		return orderService.getOrdersByFilters(parsedDate, cleanStatus);
	}
	
	@GetMapping("/getallorders")
	public List<PurchaseOrder> viewOrderHistory(
	        @RequestHeader("Authorization") String authHeader,
	        @RequestParam(required = false) String date,
	        @RequestParam(required = false) String status) {

	    String token = authHeader.replace("Bearer ", "");
	    Long userId = securityUtil.extractUserId(token);

	    LocalDate parsedDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date.trim()) : null;
	    String cleanStatus = (status != null && !status.isBlank()) ? status.trim().toLowerCase() : null;

	    return orderService.getOrdersByUserAndFilters(userId, parsedDate, cleanStatus);
	}

	
    @PostMapping("/createorder")
    public ResponseEntity<PurchaseOrder> createOrder(
            @RequestBody PurchaseOrder order,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = securityUtil.extractUserId(token);
        order.setUserId(userId);

        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setPurchaseOrder(order));
        }

//        PurchaseOrder savedOrder = orderService.saveOrder(order);
        PurchaseOrder savedOrder = orderService.saveOrderAndSendEmail(order, true);
        return ResponseEntity.ok(savedOrder);
    }
    
    @GetMapping("/getorder/{id}")
    public ResponseEntity<PurchaseOrder> getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("invoice/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long orderId) {
        byte[] pdf = orderService.generateInvoicePdf(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("invoice_order_" + orderId + ".pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

//    @PutMapping("/edit/{id}")
//    public ResponseEntity<PurchaseOrder> updateOrder(@PathVariable Long id, @RequestBody PurchaseOrder updatedOrder) {
//        PurchaseOrder savedOrder = orderService.updateOrder(id, updatedOrder);
//        return ResponseEntity.ok(savedOrder);
//    }
    
    @PutMapping("/edit/{id}")
    public ResponseEntity<PurchaseOrder> updateOrder(@PathVariable Long id, @RequestBody PurchaseOrder updatedOrder) {
        PurchaseOrder savedOrder = orderService.updateOrder(id, updatedOrder);
        return ResponseEntity.ok(savedOrder);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/pdf-report")
    public ResponseEntity<byte[]> downloadAllOrdersPdf() {
        ByteArrayInputStream bis = pdfService.generateAllOrdersPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=all-orders.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }
    
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<PurchaseOrder> cancelOrder(@PathVariable Long orderId) {
        PurchaseOrder cancelledOrder = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(cancelledOrder);
    }
    
    @PutMapping("/deliver/{orderId}")
    public ResponseEntity<?> deliverOrder(@PathVariable Long orderId) {
        try {
            PurchaseOrder deliveredOrder = orderService.deliverOrder(orderId);
            return ResponseEntity.ok(deliveredOrder);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
