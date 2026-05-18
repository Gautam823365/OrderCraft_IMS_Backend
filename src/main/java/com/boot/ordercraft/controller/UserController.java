package com.boot.ordercraft.controller;

import com.boot.ordercraft.model.User;
import com.boot.ordercraft.repository.UserRepository;
import com.boot.ordercraft.service.UserService;
import com.boot.ordercraft.util.Utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200") 
@RequestMapping("/ordercraft")
public class UserController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private Utilities jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JavaMailSender mailSender;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            // Check if account is locked
//            if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
//                return ResponseEntity.status(403).body(Map.of("error", "Account is locked until " + user.getAccountLockedUntil()));
//            }
            
         // Check if account is locked
            if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
                return ResponseEntity.status(403).body(Map.of(
                    "error", "Your account has been locked until " + user.getAccountLockedUntil() + ". Please try again later."
                ));
            }


            try {
                authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

                // Successful login
                user.setLast_login(LocalDateTime.now());
                user.setFailedAttempts(0);  // ✅ Reset on success
                user.setAccountLockedUntil(null);
                userRepository.save(user);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String token = jwtUtil.generateToken(userDetails, user.getUser_id());

                return ResponseEntity.ok(Map.of("token", token));

            } catch (BadCredentialsException e) {
                int currentAttempts = user.getFailedAttempts() + 1;
                user.setFailedAttempts(currentAttempts);

                if (currentAttempts >= 3) {
                    user.setAccountLockedUntil(LocalDateTime.now().plusHours(12));
                    userRepository.save(user);
                    return ResponseEntity.status(403).body(Map.of(
//                        "error", "Account is locked until " + user.getAccountLockedUntil()
                    		 "error","Your account has been locked due to too many failed login attempts. Please try again after 12 hours."
                    ));
                } else {
                    userRepository.save(user);
                    return ResponseEntity.status(401).body(Map.of(
                        "error", "Invalid credentials",
                        "attemptsLeft", 3 - currentAttempts
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Something went wrong"));
        }
    }
    
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String newPassword = data.get("newPassword");

        try {
            boolean isReset = userService.resetPassword(username, newPassword);
            if (isReset) {
                return ResponseEntity.ok(Map.of("message", "Password reset successful"));
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Unable to reset password"));
        }
    }



    
    @PostMapping("/adduser")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            if (user.getRole() != null) {
                System.out.println("Received roleId: " + user.getRole().getRole_id());
            } else {
                System.out.println("No role provided in request.");
            }

            if (user.getAddress() != null) {
                System.out.println("Received Address: " + user.getAddress().getAddressStreet());
            } else {
                System.out.println("No address provided in request.");
            }

            Optional<User> createdUser = userService.createUser(user);

            if (createdUser.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
            }

            return ResponseEntity.ok(createdUser.get());
        } catch (Exception e) {
            e.printStackTrace(); // Full stack trace for debugging
            return ResponseEntity.status(500).body(Map.of("error", "Unable to create user"));
        }
    }

    
    @GetMapping("/getallusers")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Unable to fetch users"));
        }
    }
    
    @DeleteMapping("/deleteuser/{id}")
    public void deleteUser(@PathVariable Long id) {
    	userService.deleteUser(id);
    }
    
    @PutMapping("/updateuser/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/unlockuser/{id}")
    public ResponseEntity<?> unlockUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        user.setFailedAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User account unlocked"));
    }
    
    
    @PutMapping("/lockuser/{id}")
    public ResponseEntity<?> lockUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        user.setAccountLockedUntil(LocalDateTime.now().plusHours(12)); // lock for 12 hours (customizable)
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User account locked for 12 hours"));
    }

    
 // 1. Send OTP
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> data) {
        String username = data.get("username");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit OTP

        // Store OTP temporarily (can use cache instead of DB)
        user.setOtp(otp);
        user.setOtpGeneratedAt(LocalDateTime.now());
        userRepository.save(user);

        // Send mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("OTP for Password Reset");
        message.setText("Your OTP is: " + otp + "\nIt is valid for 10 minutes.");
        mailSender.send(message);

        return ResponseEntity.ok(Map.of("message", "OTP sent to registered email."));
    }

    // 2. Verify OTP and Reset Password
    @PostMapping("/verify-otp-reset")
    public ResponseEntity<?> verifyOtpAndResetPassword(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String otp = data.get("otp");
        String newPassword = data.get("newPassword");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid OTP"));
        }

        if (user.getOtpGeneratedAt() == null || user.getOtpGeneratedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body(Map.of("error", "OTP expired"));
        }

        // ✅ Secure password reset
        userService.resetPassword(username, newPassword);

        // Clear OTP info
        user.setOtp(null);
        user.setOtpGeneratedAt(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }

    
}


