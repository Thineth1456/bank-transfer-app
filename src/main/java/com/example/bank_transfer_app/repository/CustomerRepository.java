package com.example.bank_transfer_app.repository;

import com.example.bank_transfer_app.model.Customer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer c where c.accountNumber = :accountNumber")
    Optional<Customer> getByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("select count(c) from Customer c where c.accountNumber = :accountNumber")
    long countAccounts(@Param("accountNumber") String accountNumber);

    @Query("select count(c) from Customer c where c.email = :email")
    long countEmails(@Param("email") String email);

    @Query("select c from Customer c order by c.name")
    List<Customer> findAllOrdered();

    @Query("select c from Customer c where lower(c.name) like lower(concat('%', :name, '%'))")
    List<Customer> searchByName(@Param("name") String name);

    @Query("select sum(c.balance) from Customer c")
    BigDecimal getTotalBalance();

    @Query(value = "SELECT COUNT(*) FROM customers", nativeQuery = true)
    long countCustomersNative();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
            update Customer c
            set c.balance = c.balance - :amount
            where c.accountNumber = :accountNumber
              and c.balance >= :amount
            """)
    int debit(@Param("accountNumber") String accountNumber, @Param("amount") BigDecimal amount);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
            update Customer c
            set c.balance = c.balance + :amount
            where c.accountNumber = :accountNumber
            """)
    int credit(@Param("accountNumber") String accountNumber, @Param("amount") BigDecimal amount);
}
