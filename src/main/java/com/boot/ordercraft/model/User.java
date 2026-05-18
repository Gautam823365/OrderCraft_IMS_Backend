package com.boot.ordercraft.model;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.*;

@Entity
@Table(name = "ORDER_CRAFT_USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long user_id;

    private String username;
    private String password;
    private String email;
    private Boolean isActive;
    private Long Phoneno;
    @Column(name = "failed_attempts")
    private int failedAttempts = 0;
    
    private String otp;

	private LocalDateTime otpGeneratedAt;


    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;


	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime last_login;

    @ManyToOne
    @JoinColumn(name = "role_id")  // foreign key in OrderManagementUsers table
    private Role role;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "addressId") // foreign key in OrderManagementUsers table
    private Address address;

    public User() {
    }

    public User(Long user_id, String username, String password, String email, Boolean isActive,
			LocalDateTime last_login, Role role, Address address) {
		super();
		this.user_id = user_id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.isActive = isActive;
		this.last_login = last_login;
		this.role = role;
		this.address = address;
	}

	// Getters and setters
    
    public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public LocalDateTime getOtpGeneratedAt() {
		return otpGeneratedAt;
	}

	public void setOtpGeneratedAt(LocalDateTime otpGeneratedAt) {
		this.otpGeneratedAt = otpGeneratedAt;
	}
	
    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getAccountLockedUntil() {
        return accountLockedUntil;
    }

    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) {
        this.accountLockedUntil = accountLockedUntil;
    }

    public Long getPhoneno() {
		return Phoneno;
	}

    public void setPhoneno(Long Phoneno) {
        this.Phoneno = Phoneno;
    }

    public Long getUser_id() {
        return user_id;
    }

    public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLast_login() {
        return last_login;
    }

    public void setLast_login(LocalDateTime last_login) {
        this.last_login = last_login;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User [user_id=" + user_id + ", username=" + username + ", password=" + password +
                ", email=" + email + ", isActive=" + isActive + ", last_login=" + last_login + ", role=" + role + "]";
    }

}
