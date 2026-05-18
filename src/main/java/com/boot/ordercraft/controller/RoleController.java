package com.boot.ordercraft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boot.ordercraft.model.Role;
import com.boot.ordercraft.repository.RoleRepository;

//RoleController.java
@RestController
@RequestMapping("ordercraft/roles")
@CrossOrigin(origins = "http://localhost:4200")
public class RoleController {

 @Autowired
 private RoleRepository roleRepository;

 @GetMapping
 public List<Role> getAllRoles() {
     return roleRepository.findAll();
 }
}
