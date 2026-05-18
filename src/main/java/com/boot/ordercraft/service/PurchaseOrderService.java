package com.boot.ordercraft.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException; // ✅ correct
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import com.boot.ordercraft.model.Customers;
import com.boot.ordercraft.model.Product;
import com.boot.ordercraft.model.PurchaseOrder;
import com.boot.ordercraft.model.PurchaseOrderItem;
import com.boot.ordercraft.model.RawMaterial;
import com.boot.ordercraft.model.Suppliers;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.repository.CustomersRepository;
import com.boot.ordercraft.repository.ProductRepository;
import com.boot.ordercraft.repository.PurchaseOrderRepository;
import com.boot.ordercraft.repository.RawMaterialRepository;
import com.boot.ordercraft.repository.SuppliersRepository;
import com.boot.ordercraft.repository.UserRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import jakarta.transaction.Transactional;

import org.thymeleaf.context.Context;

@Service
public class PurchaseOrderService {
	private final PurchaseOrderRepository orderRepository;
	private final UserRepository userRepository;
	 private final TemplateEngine templateEngine;
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private SuppliersRepository supplierRepository;
    
    @Autowired
    private CustomersRepository customerRepository;
    
    @Autowired
    private RawMaterialRepository rawMaterialRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private InvoiceGenerator invoiceGenerator;

	public PurchaseOrderService(PurchaseOrderRepository orderRepository,UserRepository userRepository, TemplateEngine templateEngine) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.templateEngine = templateEngine;
	}

	public List<PurchaseOrder> getAllOrders() {
		return orderRepository.findAll();
	}

	public List<PurchaseOrder> getOrdersByFilters(LocalDate orderDate, String status) {
		String formattedDate = (orderDate != null) ? orderDate.toString() : null;
		return orderRepository.findByFilters(formattedDate, status);
	}
	
	public List<PurchaseOrder> getOrdersByUserAndFilters(Long userId, LocalDate date, String status) {
	    String formattedDate = (date != null) ? date.toString() : null;
	    return orderRepository.findByUserIdAndFilters(userId, formattedDate, status);
	}

	

	

	public PurchaseOrder saveOrder(PurchaseOrder purchaseOrder) {
	    System.out.println(purchaseOrder);

	    // Convert deliveryStatus to uppercase
	    if (purchaseOrder.getDeliveryStatus() != null) {
	        purchaseOrder.setDeliveryStatus(purchaseOrder.getDeliveryStatus().toUpperCase());
	    }

	    // Handle Supplier case
	    if ("SUPPLIER".equalsIgnoreCase(purchaseOrder.getOrdertype())) {
	        if (purchaseOrder.getSupplier() != null && purchaseOrder.getSupplier().getSupplier_id() != null) {
	            Long supplierId = purchaseOrder.getSupplier().getSupplier_id();
	            Suppliers supplier = supplierRepository.findById(supplierId)
	                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));
	            purchaseOrder.setSupplier(supplier);
	        } else {
	            throw new RuntimeException("Suppliers ID must be provided for supplier orders");
	        }
	    }

	    // Handle Customer case
	    if ("CUSTOMER".equalsIgnoreCase(purchaseOrder.getOrdertype())) {
	        if (purchaseOrder.getCustomer() != null && purchaseOrder.getCustomer().getCust_id() != null) {
	            Integer customerId = Math.toIntExact(purchaseOrder.getCustomer().getCust_id());
	            Customers existingCustomer = customerRepository.findById(Long.valueOf(customerId))
	                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
	            purchaseOrder.setCustomer(existingCustomer);
	        } else {
	            throw new RuntimeException("Customers ID must be provided for customer orders");
	        }
	    }

	    // Process items
	    if (purchaseOrder.getItems() != null && !purchaseOrder.getItems().isEmpty()) {
	        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
	            item.setPurchaseOrder(purchaseOrder); // back-reference

	            if ("CUSTOMER".equalsIgnoreCase(purchaseOrder.getOrdertype())) {
	                if (item.getProduct() != null && item.getProduct().getProductId() != null) {
	                    Long productId = item.getProduct().getProductId();
	                    Product product = productRepository.findById(productId)
	                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
	                    item.setProduct(product);
	                } else {
	                    throw new RuntimeException("Product ID must be provided for customer orders");
	                }
	            } else if ("SUPPLIER".equalsIgnoreCase(purchaseOrder.getOrdertype())) {
	                if (item.getRawmaterial() != null && item.getRawmaterial().getRaw_material_id() != null) {
	                    Integer rawMaterialId = item.getRawmaterial().getRaw_material_id();
	                    RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
	                        .orElseThrow(() -> new RuntimeException("Raw Material not found with ID: " + rawMaterialId));
	                    item.setRawmaterial(rawMaterial);
	                } else {
	                    throw new RuntimeException("Raw Material ID must be provided for supplier orders");
	                }
	            }
	        }
	    }

	    return orderRepository.save(purchaseOrder);
	}

	
	public PurchaseOrder saveOrderAndSendEmail(PurchaseOrder purchaseOrder, boolean paymentSuccess) {
	    PurchaseOrder savedOrder = saveOrder(purchaseOrder); // your existing save logic

	    try {
	        byte[] pdf = invoiceGenerator.generateInvoice(savedOrder, paymentSuccess);
	        String email = null;
	        String name = null;

	        if ("CUSTOMER".equalsIgnoreCase(savedOrder.getOrdertype())) {
	            Customers cust = customerRepository.findById(savedOrder.getCustomer().getCust_id()).orElseThrow();
	            email = cust.getCust_email(); // make sure your entity has this
	            name = cust.getCust_name();
	        } else if ("SUPPLIER".equalsIgnoreCase(savedOrder.getOrdertype())) {
	            Suppliers supp = supplierRepository.findById(savedOrder.getSupplier().getSupplier_id()).orElseThrow();
	            email = supp.getContact_email();
	            name = supp.getContact_name();
	        }

	        if (email != null) {
	            String subject = "Invoice for Your Order #" + savedOrder.getItems();
	            String body = String.format("Hi %s,\n\nPlease find attached the invoice for your recent order.\n\nThank you!", name);
	            emailService.sendInvoiceEmail(email, subject, body, pdf, "invoice_order_" + savedOrder.getItems() + ".pdf");
	        }

	    } catch (Exception e) {
	        System.err.println("Email sending failed: " + e.getMessage());
	    }

	    return savedOrder;
	}
	
	
	
	
    public Optional<PurchaseOrder> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public byte[] generateInvoicePdf(Long orderId) {
        PurchaseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("order", order);

        // Render HTML using Thymeleaf
        String htmlContent = templateEngine.process("invoice", context);

        // Convert HTML to PDF
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
    
    // ✅ Delete Order
    public void deleteOrder(Long id) {
    	orderRepository.deleteById(id);
    }

    // ✅ Update Order
//    @Transactional
//    public PurchaseOrder updateOrder(Long id, PurchaseOrder updatedOrder) {
//        PurchaseOrder existing = orderRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        existing.setOrderDate(updatedOrder.getOrderDate());
//        existing.setExpectedDelivery(updatedOrder.getExpectedDelivery());
//        existing.setDeliveryStatus(updatedOrder.getDeliveryStatus());
//
//        // Load managed supplier and set
//        if (updatedOrder.getSupplier() != null && updatedOrder.getSupplier().getSupplier_id() != null) {
//            Suppliers supplier = supplierRepository.findById(updatedOrder.getSupplier().getSupplier_id())
//                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
//            existing.setSupplier(supplier);
//        } else {
//            existing.setSupplier(null);
//        }
//
//        // Map existing items by itemId for quick lookup
//        Map<Long, PurchaseOrderItem> existingItemsMap = existing.getItems().stream()
//                .collect(Collectors.toMap(PurchaseOrderItem::getItemId, item -> item));
//
//        // Prepare new list of items to be associated with existing PurchaseOrder
//        List<PurchaseOrderItem> updatedItems = new ArrayList<>();
//
//        if (updatedOrder.getItems() != null) {
//            for (PurchaseOrderItem incomingItem : updatedOrder.getItems()) {
//                PurchaseOrderItem itemEntity;
//
//                if (incomingItem.getItemId() != null && existingItemsMap.containsKey(incomingItem.getItemId())) {
//                    // Existing item - update fields
//                    itemEntity = existingItemsMap.get(incomingItem.getItemId());
//                } else {
//                    // New item - create new entity
//                    itemEntity = new PurchaseOrderItem();
//                    itemEntity.setPurchaseOrder(existing);
//                }
//
//                // Update common fields
//                itemEntity.setQuantity(incomingItem.getQuantity());
//                itemEntity.setCost(incomingItem.getCost());
//
//                // Load managed Product entity and set
//                if (incomingItem.getProduct() != null && incomingItem.getProduct().getProductId() != null) {
//                    Product product = productRepository.findById(incomingItem.getProduct().getProductId())
//                            .orElseThrow(() -> new RuntimeException("Product not found"));
//                    itemEntity.setProduct(product);
//                } else {
//                    itemEntity.setProduct(null);
//                }
//
//                updatedItems.add(itemEntity);
//
//                // Remove from existing map so remaining items after loop are deleted
//                if (incomingItem.getItemId() != null) {
//                    existingItemsMap.remove(incomingItem.getItemId());
//                }
//            }
//        }
//
//        // Delete items that no longer exist in incoming update
//        existingItemsMap.values().forEach(itemToRemove -> {
//            existing.getItems().remove(itemToRemove);
//        });
//
//        // Set updated items list
//        existing.setItems(updatedItems);
//
//        return orderRepository.save(existing);
//    }
   
    @Transactional
    public PurchaseOrder updateOrder(Long id, PurchaseOrder updatedOrder) {
        PurchaseOrder existing = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        existing.setOrderDate(updatedOrder.getOrderDate());
        existing.setExpectedDelivery(updatedOrder.getExpectedDelivery());
        existing.setDeliveryStatus(updatedOrder.getDeliveryStatus());
        existing.setOrdertype(updatedOrder.getOrdertype());

        // Set supplier if present
        if (updatedOrder.getSupplier() != null && updatedOrder.getSupplier().getSupplier_id() != null) {
            Suppliers supplier = supplierRepository.findById(updatedOrder.getSupplier().getSupplier_id())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            existing.setSupplier(supplier);
        } else {
            existing.setSupplier(null);
        }

        // Map existing items by itemId for quick lookup
        Map<Long, PurchaseOrderItem> existingItemsMap = existing.getItems().stream()
                .collect(Collectors.toMap(PurchaseOrderItem::getItemId, item -> item));

        List<PurchaseOrderItem> updatedItems = new ArrayList<>();

        if (updatedOrder.getItems() != null) {
            for (PurchaseOrderItem incomingItem : updatedOrder.getItems()) {
                PurchaseOrderItem itemEntity;

                if (incomingItem.getItemId() != null && existingItemsMap.containsKey(incomingItem.getItemId())) {
                    itemEntity = existingItemsMap.get(incomingItem.getItemId());
                } else {
                    itemEntity = new PurchaseOrderItem();
                    itemEntity.setPurchaseOrder(existing);
                }

                // Set shared fields
                itemEntity.setQuantity(incomingItem.getQuantity());
                itemEntity.setCost(incomingItem.getCost());

                // Reset associations
                itemEntity.setProduct(null);
                itemEntity.setRawmaterial(null);

                // Conditional assignment based on ordertype
                if ("CUSTOMER".equalsIgnoreCase(updatedOrder.getOrdertype())) {
                    if (incomingItem.getProduct() != null && incomingItem.getProduct().getProductId() != null) {
                        Product product = productRepository.findById(incomingItem.getProduct().getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));
                        itemEntity.setProduct(product);
                    }
                } else if ("SUPPLIER".equalsIgnoreCase(updatedOrder.getOrdertype())) {
                    if (incomingItem.getRawmaterial() != null && incomingItem.getRawmaterial().getRaw_material_id() != null) {
                        RawMaterial rawMaterial = rawMaterialRepository.findById(incomingItem.getRawmaterial().getRaw_material_id())
                                .orElseThrow(() -> new RuntimeException("Raw material not found"));
                        itemEntity.setRawmaterial(rawMaterial);
                    }
                }

                updatedItems.add(itemEntity);

                // Remove processed item from existing map
                if (incomingItem.getItemId() != null) {
                    existingItemsMap.remove(incomingItem.getItemId());
                }
            }
        }

        // Remove deleted items
        existingItemsMap.values().forEach(itemToRemove -> {
            existing.getItems().remove(itemToRemove);
        });

        existing.setItems(updatedItems);

        return orderRepository.save(existing);
    }
    
    
    public PurchaseOrder cancelOrder(Long orderId) {
        PurchaseOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if already delivered or canceled
        if ("DELIVERED".equalsIgnoreCase(order.getDeliveryStatus())) {
            throw new RuntimeException("You Cannot cancel a delivered order.");
        }
        if ("CANCELLED".equalsIgnoreCase(order.getDeliveryStatus())) {
            throw new RuntimeException("Order is already cancelled.");
        }

        // Set status to cancelled
        order.setDeliveryStatus("CANCELLED");
        return orderRepository.save(order);
    }
    

    @Transactional
    public PurchaseOrder deliverOrder(Long orderId) {
        PurchaseOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        // check stockS
        for (PurchaseOrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getProductQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stocks for product: " + product.getProductName());
            }
        }

        // deduct stock
        for (PurchaseOrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setProductQuantity(product.getProductQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        // update order status
        order.setDeliveryStatus("DELIVERED");
        return orderRepository.save(order);
    }



}
