package com.boot.ordercraft.controller;

import com.boot.ordercraft.model.DemandedProductDTO;
import com.boot.ordercraft.model.Product;
import com.boot.ordercraft.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ✅ Get all products
//    @GetMapping("/getallproducts")
//    public List<Product> getAllProducts() {
//        return productService.getAllProducts();
//    }
    
    @GetMapping("/getallproducts")
    public List<Product> getAllProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long categoryId
    ) {
        return productService.getFilteredProducts(productName, categoryId);
    }

    // ✅ Create product
    @PostMapping("/addproduct")
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    // ✅ Update product
    @PutMapping("/updateproduct/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete product
    @DeleteMapping("/deleteproduct/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
//    @PatchMapping("/updateDemand/{id}")
//    public ResponseEntity<Product> updateDemandedQuantity(
//            @PathVariable Long id,
//            @RequestParam Long demandedQuantity) {
//
//        return productService.updateDemandedQuantity(id, demandedQuantity)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
    
//    @PatchMapping("/updateDemand/{productId}")
//    public ResponseEntity<Product> updateDemand(
//            @PathVariable Long productId,
//            @RequestParam Long demandedQuantity, // query param
//            @RequestParam Long userId) {
//        return productService.updateDemandedQuantity(productId, demandedQuantity, userId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    
//    @GetMapping("/demanded/count")
//    public long getDemandedProductsCount() {
//        return productService.countProductsWithDemand();
//    }
    

//@GetMapping("/demanded")
//public List<Product> getDemandedProducts() {
//    return productService.getProductsWithDemand();
//}
    
    @GetMapping("/demanded")
    public List<DemandedProductDTO> getDemandedProducts() {
        return productService.getDemandedProductsWithStatus();
    }


}
