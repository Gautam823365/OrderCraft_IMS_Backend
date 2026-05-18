package com.boot.ordercraft.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.boot.ordercraft.dto.UpdateUserDto;
import com.boot.ordercraft.model.User;
import com.boot.ordercraft.service.ProfileService;

@RestController
@RequestMapping("/ordercraft")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<User> getLoggedInUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            
            Object principal = authentication.getPrincipal();
            String username;

            if (principal instanceof UserDetails userDetails) {
                username = userDetails.getUsername();
            } else {
                username = principal.toString(); // fallback (rare case)
            }

            Optional<User> user = profileService.getByUsername(username); // rename from 'profile' to 'user'
            return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        }

        return ResponseEntity.status(401).build(); // 401 Unauthorized
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody UpdateUserDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = (authentication.getPrincipal() instanceof UserDetails userDetails)
                          ? userDetails.getUsername()
                          : authentication.getPrincipal().toString();

        Optional<User> userOpt = profileService.getByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        Optional<User> updatedUser = profileService.updateUserWithRoleName(userOpt.get().getUser_id(), dto);
        return updatedUser.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

}
