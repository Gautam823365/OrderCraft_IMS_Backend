//package com.boot.ordercraft.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.boot.ordercraft.model.RawMaterial;
//import com.boot.ordercraft.model.Role;
//import com.boot.ordercraft.repository.RawMaterialRepository;
//import com.boot.ordercraft.repository.RoleRepository;
//
//@RestController
//@RequestMapping("/api/orders")
////@CrossOrigin(origins = "http://localhost:53898")
//@CrossOrigin(origins = "http://localhost:4200")
//public class RawMaterialController {
//
//	 @Autowired
//	 private RawMaterialRepository rawMaterialRepository;
//
//	 @GetMapping("/getallrawmaterials")
//	   public List<RawMaterial> getAllRawMaterials() {
//	      return rawMaterialRepository.findAll();
//	 }
//	
//	 @PostMapping("/addrawmaterial")
//	    public RawMaterial addRawMaterial(@RequestBody RawMaterial rawMaterial) {
//	        return rawMaterialRepository.save(rawMaterial);
//	    }
//}






package com.boot.ordercraft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.boot.ordercraft.model.RawMaterial;
import com.boot.ordercraft.service.RawMaterialService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class RawMaterialController {

    @Autowired
    private RawMaterialService rawMaterialService;

    @GetMapping("/getallrawmaterials")
    public List<RawMaterial> getAllRawMaterials() {
        return rawMaterialService.getAllRawMaterials();
    }

    @PostMapping("/addrawmaterial")
    public RawMaterial addRawMaterial(@RequestBody RawMaterial rawMaterial) {
        return rawMaterialService.saveRawMaterial(rawMaterial);
    }
    
    // ✅ Get by ID
    @GetMapping("/rawmaterial/{id}")
    public RawMaterial getRawMaterialById(@PathVariable Integer id) {
        return rawMaterialService.getRawMaterialById(id)
                .orElseThrow(() -> new RuntimeException("Raw Material not found with id " + id));
    }

    // ✅ Update
    @PutMapping("/updaterawmaterial/{id}")
    public RawMaterial updateRawMaterial(@PathVariable Integer id, @RequestBody RawMaterial updatedRawMaterial) {
        return rawMaterialService.updateRawMaterial(id, updatedRawMaterial);
    }

    // ✅ Delete
    @DeleteMapping("/deleterawmaterial/{id}")
    public void deleteRawMaterial(@PathVariable Integer id) {
        rawMaterialService.deleteRawMaterial(id);
//        return "Raw Material deleted successfully with id " + id;
    }
}
