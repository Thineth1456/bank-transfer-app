package com.example.bank_transfer_app.service;

import com.example.bank_transfer_app.model.BankTransaction;
import com.example.bank_transfer_app.model.TransactionStatus;
import com.example.bank_transfer_app.repository.BankTransactionRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionAuditService {

    private final BankTransactionRepository bankTransactionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BankTransaction recordFailedTransfer(
            String sourceAccountNumber,
            String targetAccountNumber,
            BigDecimal amount,
            String errorMessage) {
        BankTransaction transaction = new BankTransaction();
        transaction.setSourceAccountNumber(sourceAccountNumber);
        transaction.setTargetAccountNumber(targetAccountNumber);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setErrorMessage(errorMessage);
        return bankTransactionRepository.save(transaction);
    }
}
