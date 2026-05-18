package com.boot.ordercraft.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_CRAFT_SUPPLIERS")
public class Suppliers {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
   private Long supplier_id;
   private String supplier_name;
   private String contact_name;
   
   @Column(name = "contact_email" , unique = true, nullable = false)  // maps DB column contact_email → Java field contactEmail
   private String contactEmail;
   
   @Column(unique = true, nullable = false)
   private String phone;
   
   private Integer rating;
   
   @Column(name = "contract_file")
   private String contractFile;
   
   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "address_id", referencedColumnName = "addressId") // foreign key in OrderManagementUsers table
   private Address address;
   
   @ManyToOne
   @JoinColumn(name = "user_id", referencedColumnName = "user_id") // foreign key in OrderManagementUsers table
   private User user;

public Suppliers() {
	super();
	// TODO Auto-generated constructor stub
}


public Suppliers(Long supplier_id, String supplier_name, String contact_name, String contactEmail, String phone,
		Integer rating, String contractFile, Address address, User user) {
	super();
	this.supplier_id = supplier_id;
	this.supplier_name = supplier_name;
	this.contact_name = contact_name;
	this.contactEmail = contactEmail;
	this.phone = phone;
	this.rating = rating;
	this.contractFile = contractFile;
	this.address = address;
	this.user = user;
}

public String getContractFile() {
    return contractFile;
}

public void setContractFile(String contractFile) {
    this.contractFile = contractFile;
}


public Integer getRating() {
	return rating;
}

public void setRating(Integer rating) {
	this.rating = rating;
}

public Long getSupplier_id() {
	return supplier_id;
}

public void setSupplier_id(Long supplier_id) {
	this.supplier_id = supplier_id;
}

public String getSupplier_name() {
	return supplier_name;
}

public void setSupplier_name(String supplier_name) {
	this.supplier_name = supplier_name;
}

public String getContact_name() {
	return contact_name;
}

public void setContact_name(String contact_name) {
	this.contact_name = contact_name;
}

public String getContact_email() {
	return contactEmail;
}

public void setContact_email(String contactEmail) {
	this.contactEmail = contactEmail;
}

public String getPhone() {
	return phone;
}

public void setPhone(String phone) {
	this.phone = phone;
}

public Address getAddress() {
	return address;
}

public void setAddress(Address address) {
	this.address = address;
}

public User getUser() {
	return user;
}

public void setUser(User user) {
	this.user = user;
}

@Override
public String toString() {
	return "Suppliers [supplier_id=" + supplier_id + ", supplier_name=" + supplier_name + ", contact_name="
			+ contact_name + ", contact_email=" + contactEmail + ", phone=" + phone + ", address=" + address
			+ ", user=" + user + "]";
}
   
   
}
