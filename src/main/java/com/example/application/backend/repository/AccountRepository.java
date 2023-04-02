package com.example.application.backend.repository;

import com.example.application.backend.entity.Account;
import com.example.application.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Query(value = "SELECT a FROM Account a WHERE a.user_uuid = :searchTerm")
    List<Account> searchAccountsByUserUuid(@Param("searchTerm") String searchTerm);

    @Query(value = "SELECT a FROM Account a WHERE a.account_id = :searchTerm")
    Account searchAccountByAccountId(@Param("searchTerm") Long searchTerm);
}
