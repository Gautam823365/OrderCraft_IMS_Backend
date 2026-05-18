package com.boot.ordercraft.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name="ORDERCRAFT_RETURN_ORDERS")
public class ReturnOrder {
  @Id
//  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @SequenceGenerator(name = "return_order_seq", sequenceName = "return_order_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "return_order_seq")
  @Column(name="RID")
  private Long rId;
  
  private Date rdate;
  private String rreason;
  private String rstatus;
  
  @ManyToOne
  @JoinColumn(name = "PURCHASE_ORDER_ID")
  private PurchaseOrder purchaseOrder;


  @ManyToOne
    @JoinColumn(name = "user_id")
    private User returnedBy;
  
  @OneToMany(mappedBy = "returnOrder", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<ReturnOrderItem> items;


  public ReturnOrder() {
	super();
	// TODO Auto-generated constructor stub
   }


public ReturnOrder(Long rId, Date rdate, String rreason, String rstatus, PurchaseOrder purchaseOrder, User returnedBy,
		List<ReturnOrderItem> items) {
	super();
	this.rId = rId;
	this.rdate = rdate;
	this.rreason = rreason;
	this.rstatus = rstatus;
	this.purchaseOrder = purchaseOrder;
	this.returnedBy = returnedBy;
	this.items = items;
}


public Long getRid() {
	return rId;
}


public void setRid(Long rid) {
	this.rId = rid;
}


public Date getRdate() {
	return rdate;
}


public void setRdate(Date rdate) {
	this.rdate = rdate;
}


public String getRreason() {
	return rreason;
}


public void setRreason(String rreason) {
	this.rreason = rreason;
}


public String getRstatus() {
	return rstatus;
}


public void setRstatus(String rstatus) {
	this.rstatus = rstatus;
}


public PurchaseOrder getPurchaseOrder() {
	return purchaseOrder;
}


public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
	this.purchaseOrder = purchaseOrder;
}


public User getReturnedBy() {
	return returnedBy;
}


public void setReturnedBy(User returnedBy) {
	this.returnedBy = returnedBy;
}


public List<ReturnOrderItem> getItems() {
	return items;
}


public void setItems(List<ReturnOrderItem> items) {
	this.items = items;
}

   

   
}
