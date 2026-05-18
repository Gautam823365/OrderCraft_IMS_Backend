package com.boot.ordercraft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.ordercraft.model.ProductionResource;
import com.boot.ordercraft.repository.ProductionResourceRepository;

@Service
public class ProductionResourceService {

	@Autowired
	private ProductionResourceRepository resourceRepository;
	
    public ProductionResource addResource(ProductionResource resource) {
        return resourceRepository.save(resource);
    }
	
}
