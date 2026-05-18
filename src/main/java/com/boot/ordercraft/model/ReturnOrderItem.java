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
@Table(name="ORDERCRAFT_RETURNORDER_ITEMS")
public class ReturnOrderItem {
  @Id
//  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @SequenceGenerator(name = "return_order_item_seq", sequenceName = "return_order_item_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "return_order_item_seq")
  private Long roiId;
  private Integer returnQuantity;
  private String conditionNote;
  
  @ManyToOne
  @JoinColumn(name = "RID")
  @JsonBackReference
  private ReturnOrder returnOrder;
  
  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

public ReturnOrderItem() {
	super();
	// TODO Auto-generated constructor stub
}

public ReturnOrderItem(Long roiId, Integer returnQuantity, String conditionNote, ReturnOrder returnOrder,
		Product product) {
	super();
	this.roiId = roiId;
	this.returnQuantity = returnQuantity;
	this.conditionNote = conditionNote;
	this.returnOrder = returnOrder;
	this.product = product;
}

public Long getRoiId() {
	return roiId;
}

public void setRoiId(Long roiId) {
	this.roiId = roiId;
}

public Integer getReturnQuantity() {
	return returnQuantity;
}

public void setReturnQuantity(Integer returnQuantity) {
	this.returnQuantity = returnQuantity;
}

public String getConditionNote() {
	return conditionNote;
}

public void setConditionNote(String conditionNote) {
	this.conditionNote = conditionNote;
}

public ReturnOrder getReturnOrder() {
	return returnOrder;
}

public void setReturnOrder(ReturnOrder returnOrder) {
	this.returnOrder = returnOrder;
}

public Product getProduct() {
	return product;
}

public void setProduct(Product product) {
	this.product = product;
}
  
  
}
