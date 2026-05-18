package com.boot.ordercraft.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.ordercraft.model.RawMaterial;
import com.boot.ordercraft.model.Suppliers;
import com.boot.ordercraft.repository.RawMaterialRepository;
import com.boot.ordercraft.repository.SuppliersRepository;

@Service
public class RawMaterialService {

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @Autowired
    private SuppliersRepository supplierRepository;

    public List<RawMaterial> getAllRawMaterials() {
        return rawMaterialRepository.findAll();
    }

    public RawMaterial saveRawMaterial(RawMaterial rawMaterial) {
        // Get supplierId from request
        Long supplierId = rawMaterial.getSupplier().getSupplier_id();

        // Attach supplier from DB
        Suppliers supplier = supplierRepository.findById(supplierId)
            .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierId));

        // Set supplier back to rawMaterial
        rawMaterial.setSupplier(supplier);

        return rawMaterialRepository.save(rawMaterial);
    }
    
    public Optional<RawMaterial> getRawMaterialById(Integer id) {
        return rawMaterialRepository.findById(id);
    }

    public RawMaterial updateRawMaterial(Integer id, RawMaterial updatedRawMaterial) {
        return rawMaterialRepository.findById(id).map(rawMaterial -> {
            rawMaterial.setMaterial_name(updatedRawMaterial.getMaterial_name());
            rawMaterial.setDescription(updatedRawMaterial.getDescription());
            rawMaterial.setUnit_of_measure(updatedRawMaterial.getUnit_of_measure());
            rawMaterial.setPrice(updatedRawMaterial.getPrice());
            rawMaterial.setSupplier(updatedRawMaterial.getSupplier());
            return rawMaterialRepository.save(rawMaterial);
        }).orElseThrow(() -> new RuntimeException("Raw Material not found with id " + id));
    }

    public void deleteRawMaterial(Integer id) {
        if (!rawMaterialRepository.existsById(id)) {
            throw new RuntimeException("Raw Material not found with id " + id);
        }
        rawMaterialRepository.deleteById(id);
    }
}
