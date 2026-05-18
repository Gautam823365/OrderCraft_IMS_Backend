package com.boot.ordercraft.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.ordercraft.model.ProductCategory;
import com.boot.ordercraft.repository.ProductCategoryRepository;

@Service
public class ProductCategoryService {
	@Autowired
	   private  ProductCategoryRepository categoryRepository;
	

    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public ProductCategory addCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }
}
