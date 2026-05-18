package com.boot.ordercraft.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "OC_PURCHASE_ORDER_ITEMS")
public class PurchaseOrderItem {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq_gen")
	@SequenceGenerator(name = "item_seq_gen", sequenceName = "OC_PURCHASE_ORDER_ITEMS_SEQ", allocationSize = 1)
	private Long itemId;

	@ManyToOne
	@JoinColumn(name = "purchase_order_id")
	@JsonBackReference
	private PurchaseOrder purchaseOrder;

	@ManyToOne
	@JoinColumn(name = "product_id")
//    @JsonBackReference
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "rawmaterial_id")
	private RawMaterial rawmaterial;

	private Double quantity;
	private Double cost;

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public RawMaterial getRawmaterial() {
		return rawmaterial;
	}

	public void setRawmaterial(RawMaterial rawmaterial) {
		this.rawmaterial = rawmaterial;
	}
	
	

}
