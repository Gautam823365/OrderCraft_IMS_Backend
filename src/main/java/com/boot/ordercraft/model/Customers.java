package com.boot.ordercraft.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="OrderCraft_Customers")
public class Customers {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	
	private Long cust_id;
	private String cust_name;
	private Long cust_phoneno;
	private String cust_email;
	   @Column(name = "CUST_STREET")
	    private String street;

	    @Column(name = "CUST_CITY")
	    private String city;

	    @Column(name = "CUST_STATE")
	    private String state;

	    @Column(name = "CUST_POSTALCODE")
	    private String postalcode;

	    @Column(name = "CUST_COUNTRY")
	    private String country;
	    
		public Customers() {
			super();
			// TODO Auto-generated constructor stub
		}

		public Customers(Long cust_id, String cust_name, Long cust_phoneno, String cust_email, String street,
				String city, String state, String postalcode, String country) {
			super();
			this.cust_id = cust_id;
			this.cust_name = cust_name;
			this.cust_phoneno = cust_phoneno;
			this.cust_email = cust_email;
			this.street = street;
			this.city = city;
			this.state = state;
			this.postalcode = postalcode;
			this.country = country;
		}

		public Long getCust_id() {
			return cust_id;
		}

		public void setCust_id(Long cust_id) {
			this.cust_id = cust_id;
		}

		public String getCust_name() {
			return cust_name;
		}

		public void setCust_name(String cust_name) {
			this.cust_name = cust_name;
		}

		public Long getCust_phoneno() {
			return cust_phoneno;
		}

		public void setCust_phoneno(Long cust_phoneno) {
			this.cust_phoneno = cust_phoneno;
		}

		public String getCust_email() {
			return cust_email;
		}

		public void setCust_email(String cust_email) {
			this.cust_email = cust_email;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getPostalcode() {
			return postalcode;
		}

		public void setPostalcode(String postalcode) {
			this.postalcode = postalcode;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}
		
	
}
