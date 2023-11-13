package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
@AllArgsConstructor
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Customer> listCustomer() {
        log.debug("Returning the list of customers");
        return customerService.listCustomer();
    }

    @RequestMapping(value = "/{customerID}", method = RequestMethod.GET)
    public Customer getCustomerById(@PathVariable("customerID") UUID customerId) {
        log.debug("Returning the customer by id. Id: " + customerId);
        return customerService.getCustomerById(customerId);
    }

}
