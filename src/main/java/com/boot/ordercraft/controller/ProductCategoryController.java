package com.boot.ordercraft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boot.ordercraft.model.ProductCategory;
import com.boot.ordercraft.repository.ProductCategoryRepository;
import com.boot.ordercraft.service.ProductCategoryService;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductCategoryController {
	
	@Autowired
	private ProductCategoryService categoryService;
   
    @GetMapping("/getcategories")
    public List<ProductCategory> getCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/addcategory")
    public ProductCategory addCategory(@RequestBody ProductCategory category) {
    	System.out.println("Incoming category: " + category.getCategoryName());
        return categoryService.addCategory(category);
    }

}
