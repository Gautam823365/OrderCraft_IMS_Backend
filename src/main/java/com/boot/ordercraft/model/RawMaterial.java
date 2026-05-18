package com.boot.ordercraft.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="ORDERCRAFT_RAWMATERIAL")
public class RawMaterial {
	
	@Id
//	@GeneratedValue(strategy= GenerationType.AUTO)
	
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "raw_material_seq")
	@SequenceGenerator(
	    name = "raw_material_seq",
	    sequenceName = "RAW_MATERIAL_SEQ",  // matches Oracle existing sequence
	    allocationSize = 1                  // keep in sync with Oracle sequence
	)

	
	private Integer raw_material_id;
	private String material_name;
	private String description;
	private String unit_of_measure;
	private Double price;
	
	@ManyToOne
	@JoinColumn(name="supplier_id")
	private Suppliers supplier;
	
	public RawMaterial() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RawMaterial(Integer raw_material_id, String material_name, String description, String unit_of_measure,
			Double price,Suppliers supplier) {
		super();
		this.raw_material_id = raw_material_id;
		this.material_name = material_name;
		this.description = description;
		this.unit_of_measure = unit_of_measure;
		this.price = price;
		this.supplier = supplier;
	}

	public Integer getRaw_material_id() {
		return raw_material_id;
	}
	public void setRaw_material_id(Integer raw_material_id) {
		this.raw_material_id = raw_material_id;
	}
	public String getMaterial_name() {
		return material_name;
	}
	public void setMaterial_name(String material_name) {
		this.material_name = material_name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUnit_of_measure() {
		return unit_of_measure;
	}
	public void setUnit_of_measure(String unit_of_measure) {
		this.unit_of_measure = unit_of_measure;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Suppliers getSupplier() {
		return supplier;
	}
	public void setSupplier(Suppliers supplier) {
		this.supplier = supplier;
	}
	
	

}
