package com.boot.ordercraft.controller;

import com.boot.ordercraft.model.Suppliers;
import com.boot.ordercraft.service.SuppliersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class SuppliersController {

    @Autowired
    private SuppliersService suppliersService;

    // Create a supplier
    @PostMapping("/suppliers/createsupplier")
    public Suppliers createSupplier(@RequestBody Suppliers supplier) {
        return suppliersService.saveSupplier(supplier);
    }

    // Get all suppliers
    @GetMapping("/suppliers/getallsuppliers")
    public List<Suppliers> getAllSuppliers() {
        return suppliersService.getAllSuppliers();
    }

    // Update supplier
    @PutMapping("/suppliers/{id}")
    public Suppliers updateSupplier(@PathVariable Long id, @RequestBody Suppliers supplier) {
        return suppliersService.updateSupplier(id, supplier);
    }

    // Delete supplier
    @DeleteMapping("/suppliers/{id}")
    public void deleteSupplier(@PathVariable Long id) {
        suppliersService.deleteSupplier(id);
    }

    @PostMapping("/suppliers/{id}/uploadContract")
    public ResponseEntity<Map<String, String>> uploadContract(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            suppliersService.uploadContract(id, file);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Contract uploaded successfully.");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to upload contract.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Download contract
    @GetMapping("/suppliers/{id}/downloadContract")
    public ResponseEntity<Resource> downloadContract(@PathVariable Long id) throws IOException {
        Resource fileResource = suppliersService.downloadContract(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
    
    @GetMapping("/suppliers/generateContract/{id}")
    public ResponseEntity<Resource> generateContract(@PathVariable Long id) throws IOException {
        Resource contractResource = suppliersService.generateContract(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + contractResource.getFilename() + "\"")
                .body(contractResource);
    }

}
