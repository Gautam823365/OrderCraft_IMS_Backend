package com.boot.ordercraft.service;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boot.ordercraft.model.InventoryTransactions;
import com.boot.ordercraft.model.Product;
import com.boot.ordercraft.model.PurchaseOrder;
import com.boot.ordercraft.model.PurchaseOrderItem;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.repository.InventoryTransactionsRepository;
import com.boot.ordercraft.repository.ProductRepository;
import com.boot.ordercraft.repository.PurchaseOrderRepository;
import com.boot.ordercraft.repository.UserRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class InventoryTransactionsService {

    private final InventoryTransactionsRepository inventoryTransactionsRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final UserRepository userRepository;

    public InventoryTransactionsService(
            InventoryTransactionsRepository inventoryTransactionsRepository,
            ProductRepository productRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            UserRepository userRepository) {
        this.inventoryTransactionsRepository = inventoryTransactionsRepository;
        this.productRepository = productRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.userRepository = userRepository;
    }

    public List<InventoryTransactions> getAllTransactions() {
        return inventoryTransactionsRepository.findAll();
    }

    @Transactional
    public PurchaseOrder deliverOrder(Long orderId, User performedBy) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Fetch existing user from DB
        User user = userRepository.findById(performedBy.getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // check stock
        for (PurchaseOrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getProductQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
            }
        }

        // deduct stock + create transactions
        for (PurchaseOrderItem item : order.getItems()) {
            Product product = item.getProduct();

            product.setProductQuantity(product.getProductQuantity() - item.getQuantity());
            productRepository.save(product);

            InventoryTransactions txn = new InventoryTransactions();
            txn.setProduct(product);
            txn.setUserId(user); // ✅ use managed user
            txn.setTransactionDate(LocalDate.now());
            txn.setTransactionType("OUT");
            txn.setQuantity(item.getQuantity());
            txn.setReference("Order #" + order.getPurchaseOrderId());

            inventoryTransactionsRepository.save(txn);
        }

        order.setDeliveryStatus("DELIVERED");
        return purchaseOrderRepository.save(order);
    }
    
    public List<InventoryTransactions> getFilteredTransactions(
            String productName,
            String transactionType,
            LocalDate startDate,
            LocalDate endDate,
            String performedBy
    ) {
        return inventoryTransactionsRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (productName != null && !productName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("product").get("productName")), "%" + productName.toLowerCase() + "%"));
            }

            if (transactionType != null && !transactionType.isEmpty()) {
                predicates.add(cb.equal(root.get("transactionType"), transactionType));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("transactionDate"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), endDate));
            }

            if (performedBy != null && !performedBy.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("userId").get("username")), "%" + performedBy.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
    

}
