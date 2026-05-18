package com.boot.ordercraft.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ORDERCRAFT_PRODUCRESOURCE")
public class ProductionResource {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resource_seq")
    @SequenceGenerator(
        name = "resource_seq",            // Generator name (must match in @GeneratedValue)
        sequenceName = "RESOURCE_SEQ",    // Actual DB sequence name
        allocationSize = 1                // Increment step (1 means no gaps)
    )
    private Long id;

    private String resourceName;
    private String status; // AVAILABLE / BUSY

    public ProductionResource() {}

    public ProductionResource(String resourceName, String status) {
        this.resourceName = resourceName;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
