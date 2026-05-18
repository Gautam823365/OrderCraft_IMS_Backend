package com.boot.ordercraft.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="OrderCraft_ProductionSch")
public class ProductionSchedule {
 
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer psId;
	
	private LocalDate psStartDate;
	private LocalDate psEndDate;
	 private LocalDate psDeadline; 
	private Integer psQuantity;
	private String psStatus;
	private Integer completedQuantity;   // default 0
	private Integer qcBufferHours;       // optional, e.g. 2 hours
	
	@ManyToOne
	@JoinColumn(name = "productId", nullable = false)
	private Product psProductId;
	
	@ManyToOne
	@JoinColumn(name = "resourceId")
	private ProductionResource resource;
	
	   // ✅ Back-reference to ProductDemand
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProductDemand> demands = new ArrayList<>();


	public ProductionSchedule() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProductionSchedule(Integer psId, LocalDate psStartDate, LocalDate psEndDate, LocalDate psDeadline,
			Integer psQuantity, String psStatus, Integer completedQuantity, Integer qcBufferHours, Product psProductId,
			ProductionResource resource, List<ProductDemand> demands) {
		super();
		this.psId = psId;
		this.psStartDate = psStartDate;
		this.psEndDate = psEndDate;
		this.psDeadline = psDeadline;
		this.psQuantity = psQuantity;
		this.psStatus = psStatus;
		this.completedQuantity = completedQuantity;
		this.qcBufferHours = qcBufferHours;
		this.psProductId = psProductId;
		this.resource = resource;
		this.demands = demands;
	}

	public List<ProductDemand> getDemands() {
		return demands;
	}

	public void setDemands(List<ProductDemand> demands) {
		this.demands = demands;
	}

	public LocalDate getPsDeadline() {
        return psDeadline;
    }

    public void setPsDeadline(LocalDate psDeadline) {
        this.psDeadline = psDeadline;
    }
	
	public ProductionResource getResource() {
		return resource;
	}


	public void setResource(ProductionResource resource) {
		this.resource = resource;
	}


	public Integer getCompletedQuantity() {
		return completedQuantity;
	}


	public void setCompletedQuantity(Integer completedQuantity) {
		this.completedQuantity = completedQuantity;
	}


	public Integer getQcBufferHours() {
		return qcBufferHours;
	}


	public void setQcBufferHours(Integer qcBufferHours) {
		this.qcBufferHours = qcBufferHours;
	}


	public String getPsStatus() {
		return psStatus;
	}

	public void setPsStatus(String psStatus) {
		this.psStatus = psStatus;
	}

	public Integer getPsId() {
		return psId;
	}

	public void setPsId(Integer psId) {
		this.psId = psId;
	}

	public LocalDate getPsStartDate() {
		return psStartDate;
	}

	public void setPsStartDate(LocalDate psStartDate) {
		this.psStartDate = psStartDate;
	}

	public LocalDate getPsEndDate() {
		return psEndDate;
	}

	public void setPsEndDate(LocalDate psEndDate) {
		this.psEndDate = psEndDate;
	}

	public Integer getPsQuantity() {
		return psQuantity;
	}

	public void setPsQuantity(Integer psQuantity) {
		this.psQuantity = psQuantity;
	}

	public Product getPsProductId() {
		return psProductId;
	}

	public void setPsProductId(Product psProductId) {
		this.psProductId = psProductId;
	}

	
	
	
	
}
