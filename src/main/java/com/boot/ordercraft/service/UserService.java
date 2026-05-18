package com.boot.ordercraft.service;

import com.boot.ordercraft.dto.UpdateUserDto;
import com.boot.ordercraft.model.Address;
import com.boot.ordercraft.model.Role;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.repository.AddressRepository;
import com.boot.ordercraft.repository.RoleRepository;
import com.boot.ordercraft.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String rawRoleName = user.getRole().getRoleName();
        String normalizedRole = rawRoleName.trim().toUpperCase().replace(" ", "_");
        if (!normalizedRole.startsWith("ROLE_")) {
            normalizedRole = "ROLE_" + normalizedRole;
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(normalizedRole))
        );
    }

    public Optional<User> createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return Optional.empty();
        }

        // Resolve and persist role
        Role role = roleRepository.findById(user.getRole().getRole_id())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        user.setLast_login(null);

        // Address is cascaded, but ensure it's not detached
        if (user.getAddress() != null && user.getAddress().getAddressId() == null) {
            Address savedAddress = addressRepository.save(user.getAddress());
            user.setAddress(savedAddress);
        }

        User savedUser = userRepository.save(user);
        sendCredentialsMail(savedUser.getEmail(), savedUser.getUsername(), user.getPassword());

        return Optional.of(savedUser);
    }

    private void sendCredentialsMail(String toEmail, String username, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your Account Credentials");
            message.setText("Dear user,\n\nYour account has been created.\n\nUsername: " + username + "\nPassword: " + password + "\n\nRegards,\nTeam");

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean resetPassword(String username, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) return false;

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneno(updatedUser.getPhoneno());

        Role role = roleRepository.findById(updatedUser.getRole().getRole_id())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        existingUser.setRole(role);

        if (updatedUser.getAddress() != null) {
            if (updatedUser.getAddress().getAddressId() != null) {
                Address address = addressRepository.findById(updatedUser.getAddress().getAddressId())
                        .orElseThrow(() -> new RuntimeException("Address not found"));
                existingUser.setAddress(address);
            } else {
                Address newAddress = addressRepository.save(updatedUser.getAddress());
                existingUser.setAddress(newAddress);
            }
        }

        return userRepository.save(existingUser);
    }
    //edit profile
    
   


}
