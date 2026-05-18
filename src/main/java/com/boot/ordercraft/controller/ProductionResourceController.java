package com.boot.ordercraft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boot.ordercraft.model.ProductionResource;
import com.boot.ordercraft.service.ProductionResourceService;

@RestController
@RequestMapping("/api/orders/resources")
public class ProductionResourceController {

	@Autowired
	 private  ProductionResourceService resourceService;
	 
	    @PostMapping("/addresource")
	    public ResponseEntity<ProductionResource> addResource(@RequestBody ProductionResource resource) {
	        ProductionResource saved = resourceService.addResource(resource);
	        return ResponseEntity.ok(saved);
	    }
}
