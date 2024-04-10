package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.CustomerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    List<CustomerDTO> listCustomer();
    Optional<CustomerDTO> getCustomerById(UUID id);

    CustomerDTO saveNewCustomer(CustomerDTO customerDTO);

    Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customerDTO);

    Boolean deleteById(UUID customerId);

    Optional<CustomerDTO> patchById(UUID customerId, CustomerDTO customerDTO);
}
