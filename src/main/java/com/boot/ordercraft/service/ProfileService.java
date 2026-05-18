package com.boot.ordercraft.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.ordercraft.dto.UpdateUserDto;
import com.boot.ordercraft.model.Role;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.repository.RoleRepository;
import com.boot.ordercraft.repository.UserRepository;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setPhoneno(updatedUser.getPhoneno());
            return userRepository.save(user);
        });
    }
    public Optional<User> updateUserWithRoleName(Long id, UpdateUserDto dto) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneno(dto.getPhoneno());

        Optional<Role> roleOpt = roleRepository.findByRoleName(dto.getRoleName());
        if (roleOpt.isEmpty()) {
            // Handle missing role (you can throw exception or return empty)
            return Optional.empty();
        }

        user.setRole(roleOpt.get()); // ✅ Assign the actual Role object
        userRepository.save(user);
        return Optional.of(user);
    }
}
