package com.boot.ordercraft.model;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDERCRAFT_PURCHASE_ORDERS")
public class PurchaseOrder {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq_gen")
	@SequenceGenerator(name = "item_seq_gen", sequenceName = "ORDERCRAFT_PURCHASE_ORDERS_SEQ", allocationSize = 1)
    private Long purchaseOrderId;

    private Long userId; // Procurement officer
    private String ordertype;
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Suppliers supplier;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")  
    private Customers customer;

    private LocalDate orderDate;
    private LocalDate expectedDelivery;
    private String deliveryStatus;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PurchaseOrderItem> items;

//    @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
//    private DeliveryTracking deliveryTracking;

	public Long getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Long purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public Long getUser() {
		return userId;
	}

	public void setUserId(Long userId2) {
		this.userId = userId2;
	}
//	public User getUser() {
//	    return user;
//	}
//
//	public void setUser(User user) {
//	    this.user = user;
//	}

	
	public LocalDate getOrderDate() {
		return orderDate;
	}

	public Suppliers getSupplier() {
		return supplier;
	}

	public void setSupplier(Suppliers supplier) {
		this.supplier = supplier;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public LocalDate getExpectedDelivery() {
		return expectedDelivery;
	}

	public void setExpectedDelivery(LocalDate expectedDelivery) {
		this.expectedDelivery = expectedDelivery;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public List<PurchaseOrderItem> getItems() {
		return items;
	}

	public void setItems(List<PurchaseOrderItem> items) {
		this.items = items;
	}

	public Customers getCustomer() {
		return customer;
	}

	public void setCustomer(Customers customer) {
		this.customer = customer;
	}

	public String getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}
	
	
	
	

//	public DeliveryTracking getDeliveryTracking() {
//		return deliveryTracking;
//	}
//
//	public void setDeliveryTracking(DeliveryTracking deliveryTracking) {
//		this.deliveryTracking = deliveryTracking;
//	}

    
}
