package com.example.bank_transfer_app.config;

import com.example.bank_transfer_app.model.Customer;
import com.example.bank_transfer_app.repository.CustomerRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return;
        }

        customerRepository.saveAll(List.of(
                new Customer(null, "Amara Perera", "ACC1001","amara.perera@example.com", new BigDecimal("25000.00")),
                new Customer(null, "Nimal Silva", "ACC1002","nimal.silva@example.com", new BigDecimal("30000.00")),
                new Customer(null, "Kavindi Fernando", "ACC1003","kavindi.fernando@example.com", new BigDecimal("30000.00"))));
    }
}
