package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private Map<UUID, CustomerDTO> customerMap;

    public CustomerServiceImpl() {
        this.customerMap = new HashMap<>();

        CustomerDTO customerDTO1 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .customerName("John")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        CustomerDTO customerDTO2 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .customerName("Jack")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        CustomerDTO customerDTO3 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .customerName("Jim")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        customerMap.put(customerDTO1.getId(), customerDTO1);
        customerMap.put(customerDTO2.getId(), customerDTO2);
        customerMap.put(customerDTO3.getId(), customerDTO3);
    }

    @Override
    public List<CustomerDTO> listCustomer() {
        log.debug("Getting a list of customers");
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        log.debug("Getting a customer by ID. ID: " + id);
        return Optional.of(customerMap.get(id));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        CustomerDTO savedCustomerDTO = CustomerDTO.builder()
                .customerName(customerDTO.getCustomerName())
                .version(1)
                .id(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        customerMap.put(savedCustomerDTO.getId(), savedCustomerDTO);
        return savedCustomerDTO;
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customerDTO) {
        CustomerDTO existing = customerMap.get(customerId);
        existing.setCustomerName(customerDTO.getCustomerName());
        return Optional.of(existing);
    }

    @Override
    public Boolean deleteById(UUID customerId) {
        customerMap.remove(customerId);

        return true;
    }

    @Override
    public Optional<CustomerDTO> patchById(UUID customerId, CustomerDTO customerDTO) {
        CustomerDTO existing = customerMap.get(customerId);

        if (StringUtils.hasText(customerDTO.getCustomerName())) {
            existing.setCustomerName(customerDTO.getCustomerName());
        }
        if (customerDTO.getVersion() != null) {
            existing.setVersion(customerDTO.getVersion());
        }
        return Optional.of(existing);
    }
}
