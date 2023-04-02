package com.example.application.backend.service;

import com.example.application.backend.entity.Account;
import com.example.application.backend.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private static AccountRepository repo;
     public AccountService(AccountRepository repo) {
         this.repo = repo;
     }

     public static List<Account> getAccountsByUuid(String user_uuid) {
         return repo.searchAccountsByUserUuid(user_uuid);
     }

     public static void saveAccountToDatabase(Account account) {
         repo.save(account);
     }

    public static Account getAccountByAccountId(Long id) {
        return repo.searchAccountByAccountId(id);
    }

    public static void deleteAccountFromDatabase(Account account) {
         repo.delete(account);
    }
}
