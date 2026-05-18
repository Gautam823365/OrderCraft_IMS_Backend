package com.boot.ordercraft.service;

import com.boot.ordercraft.model.Product;
import com.boot.ordercraft.model.PurchaseOrder;
import com.boot.ordercraft.model.ReturnOrder;
import com.boot.ordercraft.model.ReturnOrderItem;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.repository.ProductRepository;
import com.boot.ordercraft.repository.PurchaseOrderRepository;
import com.boot.ordercraft.repository.ReturnOrderRepository;
import com.boot.ordercraft.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnOrderService {
	
	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;

//    public ReturnOrder createReturnOrder(ReturnOrder returnOrder) {
//        return returnOrderRepository.save(returnOrder);
//    }
    
//    @Transactional
//    public ReturnOrder createReturnOrder(ReturnOrder returnOrder) {
//        Long poId = returnOrder.getPurchaseOrder().getPurchaseOrderId();
//        PurchaseOrder po = purchaseOrderRepository.findById(poId)
//            .orElseThrow(() -> new RuntimeException("Purchase order not found with id " + poId));
//
//        if ("RETURNED".equalsIgnoreCase(po.getDeliveryStatus())) {
//            throw new IllegalStateException("Order already returned");
//        }
//
//        po.setDeliveryStatus("RETURNED");
//        purchaseOrderRepository.save(po);
//
//        return returnOrderRepository.save(returnOrder);
//    }
    
//    @Transactional
//    public ReturnOrder createReturnOrder(ReturnOrder returnOrderRequest) {
//        // 1. Fetch the managed PurchaseOrder entity using the ID from the request
//        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(returnOrderRequest.getPurchaseOrder().getPurchaseOrderId())
//            .orElseThrow(() -> new EntityNotFoundException("PurchaseOrder not found with id: " + returnOrderRequest.getPurchaseOrder().getPurchaseOrderId()));
//
//        // 2. Fetch the managed User entity
//        User returnedBy = userRepository.findById(returnOrderRequest.getReturnedBy().getUser_id())
//            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + returnOrderRequest.getReturnedBy().getUser_id()));
//
//        // 3. Create a new ReturnOrder object to be saved
//        ReturnOrder newReturnOrder = new ReturnOrder();
//        newReturnOrder.setRdate(returnOrderRequest.getRdate());
//        newReturnOrder.setRreason(returnOrderRequest.getRreason());
//        newReturnOrder.setRstatus(returnOrderRequest.getRstatus());
//        newReturnOrder.setPurchaseOrder(purchaseOrder); // Associate the managed entity
//        newReturnOrder.setReturnedBy(returnedBy);       // Associate the managed entity
//
//        // 4. Process the list of return items
//        List<ReturnOrderItem> newItems = returnOrderRequest.getItems().stream()
//            .map(itemRequest -> {
//                // Fetch the managed Product entity for each item
//                Product product = productRepository.findById(itemRequest.getProduct().getProductId())
//                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemRequest.getProduct().getProductId()));
//
//                ReturnOrderItem newItem = new ReturnOrderItem();
//                newItem.setProduct(product); // Associate the managed product
//                newItem.setReturnQuantity(itemRequest.getReturnQuantity());
//                newItem.setConditionNote(itemRequest.getConditionNote());
//                newItem.setReturnOrder(newReturnOrder); // Set the back-reference to the parent order
//                return newItem;
//            }).collect(Collectors.toList());
//
//        newReturnOrder.setItems(newItems);
//
//        // 5. Save the new ReturnOrder. CascadeType.ALL will automatically save the associated items.
//        return returnOrderRepository.save(newReturnOrder);
//    }

    @Transactional
    public ReturnOrder createReturnOrder(ReturnOrder returnOrderRequest) {
        // 1. Fetch the managed PurchaseOrder entity using the ID from the request
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(returnOrderRequest.getPurchaseOrder().getPurchaseOrderId())
            .orElseThrow(() -> new EntityNotFoundException("PurchaseOrder not found with id: " + returnOrderRequest.getPurchaseOrder().getPurchaseOrderId()));

        // ✅ Update purchase order status to RETURNED
        purchaseOrder.setDeliveryStatus("RETURNED");
        purchaseOrderRepository.save(purchaseOrder);

        // 2. Fetch the managed User entity
        User returnedBy = userRepository.findById(returnOrderRequest.getReturnedBy().getUser_id())
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + returnOrderRequest.getReturnedBy().getUser_id()));

        // 3. Create a new ReturnOrder object to be saved
        ReturnOrder newReturnOrder = new ReturnOrder();
        newReturnOrder.setRdate(returnOrderRequest.getRdate());
        newReturnOrder.setRreason(returnOrderRequest.getRreason());
        newReturnOrder.setRstatus(returnOrderRequest.getRstatus());
        newReturnOrder.setPurchaseOrder(purchaseOrder); // Associate updated entity
        newReturnOrder.setReturnedBy(returnedBy);

        // 4. Process the list of return items
        List<ReturnOrderItem> newItems = returnOrderRequest.getItems().stream()
            .map(itemRequest -> {
                Product product = productRepository.findById(itemRequest.getProduct().getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemRequest.getProduct().getProductId()));

                ReturnOrderItem newItem = new ReturnOrderItem();
                newItem.setProduct(product);
                newItem.setReturnQuantity(itemRequest.getReturnQuantity());
                newItem.setConditionNote(itemRequest.getConditionNote());
                newItem.setReturnOrder(newReturnOrder);
                return newItem;
            }).collect(Collectors.toList());

        newReturnOrder.setItems(newItems);

        // 5. Save the new ReturnOrder
        return returnOrderRepository.save(newReturnOrder);
    }



    public List<ReturnOrder> getAllReturnOrders() {
        return returnOrderRepository.findAll();
    }

    public ReturnOrder getReturnOrderById(Long returnId) {
        return returnOrderRepository.findById(returnId).orElse(null);
    }

    public List<ReturnOrder> filterReturnOrders(Long returnId, Date rdate, Long purchaseOrderId) {
        List<ReturnOrder> allOrders = returnOrderRepository.findAll();

        return allOrders.stream()
                .filter(order -> returnId == null || order.getRid().equals(returnId))
                .filter(order -> rdate == null || order.getRdate().equals(rdate))
                .filter(order -> purchaseOrderId == null ||
                        (order.getPurchaseOrder() != null &&
                         order.getPurchaseOrder().getPurchaseOrderId().equals(purchaseOrderId)))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ReturnOrder approveReturnOrder(Long id) {
        ReturnOrder returnOrder = returnOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return order not found"));

        if ("APPROVED".equalsIgnoreCase(returnOrder.getRstatus())) {
            throw new IllegalStateException("Already approved");
        }

        // Update product stock
        for (ReturnOrderItem item : returnOrder.getItems()) {
            Product product = item.getProduct();
            product.setProductQuantity(product.getProductQuantity() + item.getReturnQuantity());
            productRepository.save(product);
        }

        // Update return order status
        returnOrder.setRstatus("APPROVED");
        return returnOrderRepository.save(returnOrder);
    }

}
