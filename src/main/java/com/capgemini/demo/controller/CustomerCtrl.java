package com.capgemini.demo.controller;

import com.capgemini.demo.dao.CustomerRepo;
import com.capgemini.demo.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/demo")
public class CustomerCtrl {

    @Autowired
    private CustomerRepo customerRepo;

    @GetMapping("/customer/all")
    public Iterable<Customer> getAllUsers() {
        return  customerRepo.findAll();
    }

    @PostMapping("/customer")
    Customer newUser(@RequestBody Customer newCustomer) {
        return customerRepo.save(newCustomer);
    }

    @DeleteMapping("/customer/{id}")
    void deleteEmployee(@PathVariable Long id) {
        customerRepo.deleteById(id);
    }
}


