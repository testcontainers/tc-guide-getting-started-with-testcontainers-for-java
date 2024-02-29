//package com.testcontainers.demo.api;
//
//import com.testcontainers.demo.entity.Customer;
//import com.testcontainers.demo.service.CustomerService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/profile")
//public class CustomerController {
//
//    @Autowired
//    private CustomerService customerService;
//
//    @GetMapping("/customers")
//    public List<Customer> getAllCustomers() {
//        return customerService.getAllCustomers();
//    }
//}
