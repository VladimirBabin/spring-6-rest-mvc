package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@AllArgsConstructor
public class CustomerServiceJPA implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDTO> listCustomer() {
        return customerRepository.findAll().stream()
                .map(customerMapper::customerToCustomerDto)
                .toList();
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        return Optional.ofNullable(
                customerMapper.customerToCustomerDto(
                        customerRepository.findById(id).orElse(null)
                )
        );
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        return customerMapper.customerToCustomerDto(
                customerRepository.save(
                        customerMapper.customerDtoToCustomer(customerDTO))
        );
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customerDTO) {
        AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();

        customerRepository.findById(customerId).ifPresentOrElse(found -> {
            found.setCustomerName(customerDTO.getCustomerName());
            found.setLastModifiedDate(customerDTO.getLastModifiedDate());
            atomicReference.set(
                    Optional.of(
                            customerMapper.customerToCustomerDto(
                                    customerRepository.save(found))));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Override
    public Boolean deleteById(UUID customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<CustomerDTO> patchById(UUID customerId, CustomerDTO customerDTO) {
        AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();

        customerRepository.findById(customerId).ifPresentOrElse(found -> {
            if (StringUtils.hasText(customerDTO.getCustomerName()))
                found.setCustomerName(customerDTO.getCustomerName());

            if (customerDTO.getLastModifiedDate() != null)
                    found.setLastModifiedDate(customerDTO.getLastModifiedDate());
            atomicReference.set(
                    Optional.of(
                            customerMapper.customerToCustomerDto(
                                    customerRepository.save(found))));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }
}
