package com.example.bank_transfer_app.repository;

import com.example.bank_transfer_app.model.BankTransaction;
import com.example.bank_transfer_app.model.TransactionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {

    @Query("select t from BankTransaction t order by t.timestamp desc")
    List<BankTransaction> findAllLatest();

    @Query("""
            select t from BankTransaction t
            where t.sourceAccountNumber = :accountNumber
               or t.targetAccountNumber = :accountNumber
            order by t.timestamp desc
            """)
    List<BankTransaction> findHistory(@Param("accountNumber") String accountNumber);

    @Query("""
            select t from BankTransaction t
            where t.status = :status
            order by t.timestamp desc
            """)
    List<BankTransaction> findByStatusLatest(@Param("status") TransactionStatus status);

    @Query("select count(t) from BankTransaction t where t.status = :status")
    long countByStatus(@Param("status") TransactionStatus status);
}
