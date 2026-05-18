package com.boot.ordercraft.model;

public class DemandedProductDTO {
    private Long productId;
    private String productName;
    private Double productUnitPrice;
    private Long demandedQuantity;
    private String scheduleStatus; // from production_sch

    // constructor
    public DemandedProductDTO(Long productId, String productName, Double productUnitPrice, Long demandedQuantity, String scheduleStatus) {
        this.productId = productId;
        this.productName = productName;
        this.productUnitPrice = productUnitPrice;
        this.demandedQuantity = demandedQuantity;
        this.scheduleStatus = scheduleStatus;
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

	public Double getProductUnitPrice() {
		return productUnitPrice;
	}

	public void setProductUnitPrice(Double productUnitPrice) {
		this.productUnitPrice = productUnitPrice;
	}

	public Long getDemandedQuantity() {
		return demandedQuantity;
	}

	public void setDemandedQuantity(Long demandedQuantity) {
		this.demandedQuantity = demandedQuantity;
	}

	public String getScheduleStatus() {
		return scheduleStatus;
	}

	public void setScheduleStatus(String scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}

    // getters & setters
    
}

