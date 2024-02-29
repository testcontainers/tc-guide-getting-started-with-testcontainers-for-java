package com.testcontainers.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.testcontainers.demo.entity.Customer;
import com.testcontainers.demo.helper.DBConnectionProvider;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;

/*
 * Test class using the approach of defining the container as a field
 */
class CustomerServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class.getName());

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    CustomerService customerService;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        DBConnectionProvider connectionProvider = new DBConnectionProvider(
            postgres.getJdbcUrl(),
            postgres.getUsername(),
            postgres.getPassword()
        );
        customerService = new CustomerService(connectionProvider);
        customerService.clearAll();
    }

    @Test
    void shouldGetCustomers() {
        customerService.createCustomer(new Customer(1L, "George"));
        customerService.createCustomer(new Customer(2L, "John"));

        List<Customer> customers = customerService.getAllCustomers();
        assertEquals(2, customers.size());
    }

    @Test
    void shouldGet3Customers() {
        customerService.createCustomer(new Customer(1L, "George"));
        customerService.createCustomer(new Customer(2L, "John"));
        customerService.createCustomer(new Customer(3L, "Alex"));

        List<Customer> customers = customerService.getAllCustomers();
        assertEquals(3, customers.size());
    }
}
