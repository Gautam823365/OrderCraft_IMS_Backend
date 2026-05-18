package com.boot.ordercraft.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.boot.ordercraft.filter.JwtAuthFilter;

//import com.boot.ordercraft.filter.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class UserConfig {

	@Autowired
	private JwtAuthFilter jwtAuthFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	    
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/ordercraft/login").permitAll()
	            .requestMatchers("/ordercraft/reset-password").permitAll()
//	            .requestMatchers("/ordercraft/adduser").hasRole("ADMIN")
	            .requestMatchers("/ordercraft/adduser").permitAll()
	            .requestMatchers("/ordercraft/deleteuser/{id}").permitAll()
	            .requestMatchers("/ordercraft/updateuser/{id}").permitAll()
	            .requestMatchers("/ordercraft/lockuser/{id}").permitAll()
	            .requestMatchers("/ordercraft/profile").permitAll()
	          //  .requestMatchers("/ordercraft/profile/{id}").permitAll()
	            .requestMatchers("/ordercraft/profile/**").authenticated()
		        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
// customer details 
		        .requestMatchers(HttpMethod.GET, "/admin/customers/**").hasAnyRole("ADMIN")
		        .requestMatchers(HttpMethod.POST, "/admin/customers/**").hasRole("ADMIN")
		        .requestMatchers(HttpMethod.PUT, "/admin/customers/**").hasRole("ADMIN")
		        .requestMatchers(HttpMethod.DELETE, "/admin/customers/**").hasRole("ADMIN")

//	            .requestMatchers("/ordercraft/profile/**").authenticated()
	            .requestMatchers("/ordercraft/getallusers").permitAll()
	            .requestMatchers("/api/orders/**").permitAll()
	            .requestMatchers("/api/product/getcategories").permitAll()
	            .requestMatchers("/api/product/addcategory").permitAll()
	            .requestMatchers("/api/orders/getallorders").permitAll()
//	            .requestMatchers("/api/orders/addproduct").permitAll()
	            .requestMatchers("/api/orders/getallrawmaterials").permitAll()
	            .requestMatchers("/api/orders/updateDemand/{id}").permitAll()
//	            .requestMatchers("/api/orders/scheduled-products").permitAll()
	            .requestMatchers("/api/orders/getallproducts").permitAll()
	            .requestMatchers("/api/orders/createorder").permitAll()
	            .requestMatchers("/api/orders/cancel/{orderId}").permitAll()
	            .requestMatchers("/api/orders/edit/{id}").permitAll()
	            .requestMatchers("/api/orders/delete/{id}").permitAll()
	            .requestMatchers("/api/orders/getorder/{id}").permitAll()
	            .requestMatchers("/api/orders/delivery-tracking/{orderId}").permitAll()
	            .requestMatchers("/api/orders/invoice/{orderId}").permitAll()
	            .requestMatchers("/api/orders/pdf-report").permitAll()
	            .requestMatchers("/ordercraft/roles").permitAll()
//	            .requestMatchers("/ordercraft/profile").permitAll()
//	            .requestMatchers("/ordercraft/profile/{id}").permitAll()
	            .requestMatchers("/ordercraft/unlockuser/{id}").permitAll()
	            .requestMatchers("/ordercraft/send-otp").permitAll()
	            .requestMatchers("/ordercraft/verify-otp-reset").permitAll()
	            .requestMatchers("/ordercraft/suppliers/createsupplier").permitAll()
	            .requestMatchers("/api/payment/create-session").permitAll()
	            .anyRequest().authenticated()
	        )
	        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
