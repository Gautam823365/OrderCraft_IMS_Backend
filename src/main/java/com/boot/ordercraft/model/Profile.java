package com.boot.ordercraft.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Order_Management_Profile")

public class Profile {
	 @Id
	    private Long id;

	    private String username;
	    private String email;
	    private String phone;
	    private String role;
	    private String profilePictureUrl;
		public Profile() {
			super();
			// TODO Auto-generated constructor stub
		}
		public Profile(Long id, String username, String email, String phone, String role, String profilePictureUrl) {
			super();
			this.id = id;
			this.username = username;
			this.email = email;
			this.phone = phone;
			this.role = role;
			this.profilePictureUrl = profilePictureUrl;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getRole() {
			return role;
		}
		public void setRole(String role) {
			this.role = role;
		}
		public String getProfilePictureUrl() {
			return profilePictureUrl;
		}
		public void setProfilePictureUrl(String profilePictureUrl) {
			this.profilePictureUrl = profilePictureUrl;
		}
		@Override
		public String toString() {
			return "Profile [id=" + id + ", username=" + username + ", email=" + email + ", phone=" + phone + ", role="
					+ role + ", profilePictureUrl=" + profilePictureUrl + "]";
		}
	    
}
