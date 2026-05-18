package com.boot.ordercraft.service;



import org.springframework.stereotype.Service;

import com.boot.ordercraft.model.Customers;
import com.boot.ordercraft.repository.CustomersRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomersRepository customersRepository;

    public CustomerService(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    public List<Customers> getAllCustomers() {
        return customersRepository.findAll();
    }

    public Optional<Customers> getCustomerById(Long id) {
        return customersRepository.findById(id);
    }

    public Customers createCustomer(Customers customers) {
        return customersRepository.save(customers);
    }

    public Customers updateCustomer(Long id, Customers updatedCustomer) {
        return customersRepository.findById(id).map(customer -> {
        	 customer.setCust_name(updatedCustomer.getCust_name());
             customer.setCust_email(updatedCustomer.getCust_email());
             customer.setCust_phoneno(updatedCustomer.getCust_phoneno());
             customer.setStreet(updatedCustomer.getStreet());
             customer.setCity(updatedCustomer.getCity());
             customer.setState(updatedCustomer.getState());
             customer.setPostalcode(updatedCustomer.getPostalcode());
             customer.setCountry(updatedCustomer.getCountry());
             return customersRepository.save(customer);
        }).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public void deleteCustomer(Long id) {
        customersRepository.deleteById(id);
    }

}

