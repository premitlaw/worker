package com.capgemini.demo.dao;

import com.capgemini.demo.domain.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepo extends CrudRepository<Customer, Long> {

}
