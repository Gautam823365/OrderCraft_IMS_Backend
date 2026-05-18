package com.boot.ordercraft.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDERCRAFT_PRODUCT_CATEGORIES")
public class ProductCategory {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_category_seq")
    @SequenceGenerator(
        name = "product_category_seq",
        sequenceName = "PRODUCT_CATEGORY_SEQ", // database sequence name
        allocationSize = 1 // increments by 1
    )
	private Long categoryId;

	@Column(nullable = false, unique = true)
	private String categoryName;

	// Optional: one-to-many relationship
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
//    @JsonManagedReference
	private List<Product> products;

	// Getters and setters
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
