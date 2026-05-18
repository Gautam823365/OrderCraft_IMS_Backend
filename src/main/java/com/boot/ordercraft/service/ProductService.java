package com.boot.ordercraft.service;

import com.boot.ordercraft.model.DemandedProductDTO;
import com.boot.ordercraft.model.InventoryTransactions;
import com.boot.ordercraft.model.Product;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.repository.InventoryTransactionsRepository;
import com.boot.ordercraft.repository.ProductRepository;
import com.boot.ordercraft.repository.ProductionScheduleRepository;
import com.boot.ordercraft.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private InventoryTransactionsRepository inventoryTransactionsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductionScheduleRepository productionScheduleRepo;

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getFilteredProducts(String productName, Long categoryId) {
        if (productName != null && categoryId != null) {
            return productRepository.findByProductNameContainingIgnoreCaseAndCategory_CategoryId(productName, categoryId);
        } else if (productName != null) {
            return productRepository.findByProductNameContainingIgnoreCase(productName);
        } else if (categoryId != null) {
            return productRepository.findByCategory_CategoryId(categoryId);
        } else {
            return productRepository.findAll();
        }
    }


    // Create product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Update product
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setProductName(productDetails.getProductName());
                    existingProduct.setProductDescription(productDetails.getProductDescription());
                    existingProduct.setProductUnitPrice(productDetails.getProductUnitPrice());
                    existingProduct.setProductQuantity(productDetails.getProductQuantity());
                    existingProduct.setCategory(productDetails.getCategory());
                    return productRepository.save(existingProduct);
                });
    }

    // Delete product
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
    
//    public Optional<Product> updateDemandedQuantity(Long productId, Long demandedQuantity) {
//        return productRepository.findById(productId).map(product -> {
//            product.setDemandedQuantity(demandedQuantity);
//            return productRepository.save(product);
//        });
//    }
    
//    public Optional<Product> updateDemandedQuantity(Long productId, Long demandedQuantity, Long userId) {
//        return productRepository.findById(productId).map(product -> {
//        	 // ✅ Handle null safely
//            Long currentDemand = product.getDemandedQuantity() != null ? product.getDemandedQuantity() : 0L;
//            product.setDemandedQuantity(currentDemand + demandedQuantity);
//            Product updatedProduct = productRepository.save(product);
//
//            // Create transaction record
//            InventoryTransactions transaction = new InventoryTransactions();
//            transaction.setProduct(product);
//            transaction.setTransactionType("IN");
//            transaction.setQuantity(demandedQuantity);
//            transaction.setTransactionDate(LocalDate.now());
//            transaction.setReference("Demand Created");
//            transaction.setReference("Product #" + productId);
//
//            User user = userRepository.findById(userId).orElse(null);
//            transaction.setUserId(user);
//
//            inventoryTransactionsRepository.save(transaction);
//
//            return updatedProduct;
//        });
//    }
    
    public Long countProductsWithDemand() {
        return productRepository.countByDemandedQuantityGreaterThan(0L);
    }
    
    public List<Product> getProductsWithDemand() {
        return productRepository.findByDemandedQuantityGreaterThan(0L);
    }
    
    public Optional<Product> deliverProduct(Long productId) {
        return productRepository.findById(productId).map(product -> {
            if (product.getDemandedQuantity() != null && product.getDemandedQuantity() > 0) {
                double updatedQty = Optional.ofNullable(product.getProductQuantity()).orElse(0.0) 
                                     + product.getDemandedQuantity();
                product.setProductQuantity(updatedQty);
                product.setDemandedQuantity(null);
                productRepository.save(product);
                
                // ✅ Update ProductionSchedule status
                productionScheduleRepo.findByPsProductId_ProductId(productId)
                .forEach(ps -> {
                    ps.setPsStatus("DELIVERED");
                    productionScheduleRepo.save(ps);
                });
            }
            return product;
        });
    }
    
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }
    
    public List<DemandedProductDTO> getDemandedProductsWithStatus() {
        return productionScheduleRepo.findDemandedProductsWithStatus();
    }

}
