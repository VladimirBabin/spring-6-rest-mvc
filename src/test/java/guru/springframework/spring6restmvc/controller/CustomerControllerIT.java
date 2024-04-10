package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerMapper customerMapper;

    @Test
    void testListCustomers() {
        // given

        // when
        List<CustomerDTO> customerDTOS = customerController.listCustomer();

        // then
        assertThat(customerDTOS).hasSize(3);
    }

    @Transactional
    @Rollback
    @Test
    void testEmptyListCustomers() {
        // given
        customerRepository.deleteAll();

        // when
        List<CustomerDTO> customerDTOS = customerController.listCustomer();

        // then
        assertThat(customerDTOS).isEmpty();
    }

    @Test
    void testGetCustomerById() {
        // given
        UUID id = customerRepository.findAll().get(0).getId();

        // when
        CustomerDTO found = customerController.getCustomerById(id);

        // then
        assertThat(found).isNotNull();
    }

    @Test
    void testGetCustomerIdNotFound() {
        // given
        UUID id = UUID.randomUUID();

        // then
        assertThrows(NotFoundException.class, () ->  customerController.getCustomerById(id));
    }

    @Transactional
    @Rollback
    @Test
    void whenSaveNewCustomerThenCreatedAndIdInLocationHeader() {
        // given
        CustomerDTO customerDTO = CustomerDTO.builder()
                .customerName("New Customer")
                .build();

        // when
        ResponseEntity responseEntity = customerController.handlePost(customerDTO);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[3]);

        Optional<Customer> byId = customerRepository.findById(savedUUID);
        assertThat(byId).isNotEmpty();
    }

    @Transactional
    @Rollback
    @Test
    void whenUpdateByIdAndIdFoundNoContent() {
        // given
        Customer customer = customerRepository.findAll().get(0);
        UUID id = customer.getId();
        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(customer);
        customerDTO.setId(null);
        customerDTO.setVersion(null);
        final String updatedName = "UPDATED";
        customerDTO.setCustomerName(updatedName);

        // when
        ResponseEntity responseEntity = customerController.updateById(id, customerDTO);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Customer byId = customerRepository.findById(id).get();
        assertThat(byId.getCustomerName()).isEqualTo(updatedName);
    }

    @Test
    void whenUpdateByIdAndIdNotFoundThenNotFoundException() {
        // given
        UUID id = UUID.randomUUID();
        CustomerDTO dto = CustomerDTO.builder().build();


        // then
        assertThrows(NotFoundException.class, () ->  customerController.updateById(id, dto));
    }

    @Transactional
    @Rollback
    @Test
    void whenDeleteByIdAndIdFoundNoContent() {
        // given
        Customer customer = customerRepository.findAll().get(0);
        UUID id = customer.getId();

        // when
        ResponseEntity responseEntity = customerController.deleteById(id);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<Customer> byId = customerRepository.findById(id);
        assertThat(byId).isEmpty();
    }

    @Test
    void whenDeleteByIdAndIdNotFoundThenNotFoundException() {
        // given
        UUID id = UUID.randomUUID();

        // then
        assertThrows(NotFoundException.class, () ->  customerController.deleteById(id));
    }

    @Transactional
    @Rollback
    @Test
    void whenPathByIdAndIdFoundNoContent() {
        // given
        Customer customer = customerRepository.findAll().get(0);
        UUID id = customer.getId();
        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(customer);
        customerDTO.setId(null);
        customerDTO.setVersion(null);
        final String updatedName = "UPDATED";
        customerDTO.setCustomerName(updatedName);

        // when
        ResponseEntity responseEntity = customerController.patchCustomerById(id, customerDTO);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Customer byId = customerRepository.findById(id).get();
        assertThat(byId.getCustomerName()).isEqualTo(updatedName);
    }

    @Test
    void whenPatchByIdAndIdNotFoundThenNotFoundException() {
        // given
        UUID id = UUID.randomUUID();
        CustomerDTO dto = CustomerDTO.builder().build();


        // then
        assertThrows(NotFoundException.class, () ->  customerController.patchCustomerById(id, dto));
    }

}