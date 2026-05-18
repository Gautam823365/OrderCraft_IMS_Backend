package com.boot.ordercraft.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDERCRAFT_PRODUCTS")
public class Product {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private String productName;

    private String productDescription;
    private Long threshold;
    private Long demandedQuantity;

    @ManyToOne
    @JoinColumn(name = "product_category_id")
//    @JsonBackReference
    @JsonIgnoreProperties("products") 
    private ProductCategory category;

//    @ManyToOne
//    @JoinColumn(name = "products_supplier_id")
//    private Supplier supplier;

    private Double productUnitPrice;

    private Double productQuantity;
    
//    @ManyToOne
//    @JoinColumn(name="RAW_MATERIAL_ID")
//    private RawMaterials rawMaterialId;
    
    

	public Long getDemandedQuantity() {
		return demandedQuantity;
	}

	public void setDemandedQuantity(Long demandedQuantity) {
		this.demandedQuantity = demandedQuantity;
	}

	public Long getThreshold() {
		return threshold;
	}

	public void setThreshold(Long threshold) {
		this.threshold = threshold;
	}
	
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public Double getProductUnitPrice() {
		return productUnitPrice;
	}

	public void setProductUnitPrice(Double productUnitPrice) {
		this.productUnitPrice = productUnitPrice;
	}

	public Double getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(Double productQuantity) {
		this.productQuantity = productQuantity;
	}

    
}

