package guru.springframework.spring6restmvc.controller;

import com.sun.net.httpserver.Headers;
import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
@AllArgsConstructor
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity handlePost(@RequestBody Customer customer) {
        Customer customerSaved = customerService.saveNewCustomer(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "api/v1/customer/" + customerSaved.getId());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

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
