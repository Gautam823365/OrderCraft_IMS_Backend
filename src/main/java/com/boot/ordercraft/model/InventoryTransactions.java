package com.boot.ordercraft.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "OC_INVENTORY_TRANSACTIONS")
public class InventoryTransactions {
   
	    @Id
//	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_seq")
	    @SequenceGenerator(
	        name = "inventory_seq",
	        sequenceName = "OC_INVENTORY_TRANSACTIONS_SEQ", // make sure this sequence exists in Oracle
	        allocationSize = 1
	    )
	    private int transactionId;

	    @ManyToOne
	    @JoinColumn(name = "performed_by")
	    private User userId;
	    
		@ManyToOne
		@JoinColumn(name = "product_id")
//	    @JsonBackReference
		private Product product;

	    private LocalDate transactionDate;
	    private String transactionType;
	    private double quantity;
	    private String reference;
	    
		public InventoryTransactions() {
			super();
			// TODO Auto-generated constructor stub
		}

		public InventoryTransactions(int transactionId, User userId, Product product, LocalDate transactionDate,
				String transactionType, double quantity, String reference) {
			super();
			this.transactionId = transactionId;
			this.userId = userId;
			this.product = product;
			this.transactionDate = transactionDate;
			this.transactionType = transactionType;
			this.quantity = quantity;
			this.reference = reference;
		}

		public int getTransactionId() {
			return transactionId;
		}

		public void setTransactionId(int transactionId) {
			this.transactionId = transactionId;
		}

		public User getUserId() {
			return userId;
		}

		public void setUserId(User userId) {
			this.userId = userId;
		}

		public Product getProduct() {
			return product;
		}

		public void setProduct(Product product) {
			this.product = product;
		}

		public LocalDate getTransactionDate() {
			return transactionDate;
		}

		public void setTransactionDate(LocalDate transactionDate) {
			this.transactionDate = transactionDate;
		}

		public String getTransactionType() {
			return transactionType;
		}

		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}

		public double getQuantity() {
			return quantity;
		}

		public void setQuantity(double quantity) {
			this.quantity = quantity;
		}

		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}
	    
		
	    
	    
}
