package com.example.bank_transfer_app.service;

import com.example.bank_transfer_app.exception.AccountNotFoundException;
import com.example.bank_transfer_app.exception.BankException;
import com.example.bank_transfer_app.exception.InsufficientBalanceException;
import com.example.bank_transfer_app.model.BankTransaction;
import com.example.bank_transfer_app.model.Customer;
import com.example.bank_transfer_app.model.TransactionStatus;
import com.example.bank_transfer_app.repository.BankTransactionRepository;
import com.example.bank_transfer_app.repository.CustomerRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankService {

    private final CustomerRepository customerRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final TransactionAuditService transactionAuditService;

    @Transactional
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    public BankTransaction transfer(
            String sourceAccountNumber,
            String targetAccountNumber,
            BigDecimal amount) {
        try {
            if (amount == null || amount.signum() <= 0) {
                throw new BankException("Transfer amount must be greater than zero");
            }

            customerRepository.getByAccountNumber(sourceAccountNumber)
                    .orElseThrow(() -> new AccountNotFoundException(
                            "Source account not found: " + sourceAccountNumber));
            customerRepository.getByAccountNumber(targetAccountNumber)
                    .orElseThrow(() -> new AccountNotFoundException(
                            "Target account not found: " + targetAccountNumber));

            if (customerRepository.debit(sourceAccountNumber, amount) == 0) {
                throw new InsufficientBalanceException(
                        "Insufficient balance in account: " + sourceAccountNumber);
            }

            if (customerRepository.credit(targetAccountNumber, amount) == 0) {
                throw new AccountNotFoundException(
                        "Target account not found: " + targetAccountNumber);
            }

            BankTransaction transaction = new BankTransaction();
            transaction.setSourceAccountNumber(sourceAccountNumber);
            transaction.setTargetAccountNumber(targetAccountNumber);
            transaction.setAmount(amount);
            transaction.setStatus(TransactionStatus.SUCCESS);
            return bankTransactionRepository.save(transaction);
        } catch (RuntimeException exception) {
            if (sourceAccountNumber != null
                    && targetAccountNumber != null
                    && amount != null) {
                try {
                    transactionAuditService.recordFailedTransfer(
                            sourceAccountNumber,
                            targetAccountNumber,
                            amount,
                            exception.getMessage());
                } catch (RuntimeException auditException) {
                    exception.addSuppressed(auditException);
                }
            }
            throw exception;
        }
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAllOrdered();
    }

    public Customer getCustomerByAccountNumber(String accountNumber) {
        return customerRepository.getByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found: " + accountNumber));
    }

    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.searchByName(name);
    }

    public BigDecimal getTotalBalance() {
        return customerRepository.getTotalBalance();
    }

    public long getCustomerCount() {
        return customerRepository.count();
    }

    public List<BankTransaction> getAllTransactions() {
        return bankTransactionRepository.findAllLatest();
    }

    public List<BankTransaction> getTransactionHistory(String accountNumber) {
        return bankTransactionRepository.findHistory(accountNumber);
    }

    public List<BankTransaction> getTransactionsByStatus(TransactionStatus status) {
        return bankTransactionRepository.findByStatusLatest(status);
    }

    public long getTransactionCountByStatus(TransactionStatus status) {
        return bankTransactionRepository.countByStatus(status);
    }
}
