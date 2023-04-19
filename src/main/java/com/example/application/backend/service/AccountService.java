package com.example.application.backend.service;

import com.example.application.backend.entity.Account;
import com.example.application.backend.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Methods called from the AccountsView class.
 * */

@Service
public class AccountService {

    private static AccountRepository repo;
     public AccountService(AccountRepository repo) {
         this.repo = repo;
     }

     /**
      * Gets the list of accounts that correspond to the user with the passed uuid.
      * @param user_uuid The user's universally unique identifier.
      * @return The list of accounts corresponding to the user whose uuid is equal to user_uuid.
      * */
     public static List<Account> getAccountsByUuid(String user_uuid) {
         return repo.searchAccountsByUserUuid(user_uuid);
     }

     /**
      * Stores the passed Account object to the database.
      * @param account The account to be stored in the database.
      * */
     public static void saveAccountToDatabase(Account account) {
         repo.save(account);
     }

    /**
     * Gets the Account object whose primary key corresponds to the passed argument.
     * @param id The primary key of the account to be returned.
     * @return The account whose primary key is equal to id.
     * */
    public static Account getAccountByAccountId(Long id) {
        return repo.searchAccountByAccountId(id);
    }

    /**
     * Deletes an account from the database.
     * @param account The account object to be deleted.
     * */
    public static void deleteAccountFromDatabase(Account account) {
         repo.delete(account);
    }
}
