package com.charter.reward.controller;

import com.charter.reward.model.Customer;
import com.charter.reward.model.Purchase;
import com.charter.reward.repository.CustomerRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public List<Customer> createCustomers(@RequestBody List<Customer> customers) {
        return this.customerRepository.saveAll(customers);
    }

    @PostMapping("/{customerId}/purchases")
    public Customer createPurchases(@PathVariable Long customerId, @RequestBody List<Purchase> purchases) {
        log.info("Saving purchases for customer={} ", customerId);
        Customer customer = this.customerRepository.findById(customerId).get();
        purchases.forEach(customer::addToPurchases);

        return this.customerRepository.save(customer);
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return this.customerRepository.findAll();
    }
}
