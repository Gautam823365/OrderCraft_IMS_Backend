package com.boot.ordercraft.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "ORDERCRAFT_PRODUCT_DEMANDS")
public class ProductDemand {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long demandId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Long demandQuantity;
    private LocalDate demandDate;

    private String demandStatus; 
    // PENDING → waiting
    // SCHEDULED → linked to a schedule
    // FULFILLED → delivered

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = true) // optional link to schedule
    @JsonBackReference
    private ProductionSchedule schedule;

	public ProductDemand() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProductDemand(Long demandId, Product product, User user, Long demandQuantity, LocalDate demandDate,
			String demandStatus, ProductionSchedule schedule) {
		super();
		this.demandId = demandId;
		this.product = product;
		this.user = user;
		this.demandQuantity = demandQuantity;
		this.demandDate = demandDate;
		this.demandStatus = demandStatus;
		this.schedule = schedule;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getDemandId() {
		return demandId;
	}

	public void setDemandId(Long demandId) {
		this.demandId = demandId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Long getDemandQuantity() {
		return demandQuantity;
	}

	public void setDemandQuantity(Long demandQuantity) {
		this.demandQuantity = demandQuantity;
	}

	public LocalDate getDemandDate() {
		return demandDate;
	}

	public void setDemandDate(LocalDate demandDate) {
		this.demandDate = demandDate;
	}

	public String getDemandStatus() {
		return demandStatus;
	}

	public void setDemandStatus(String demandStatus) {
		this.demandStatus = demandStatus;
	}

	public ProductionSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(ProductionSchedule schedule) {
		this.schedule = schedule;
	}

    
    
}
