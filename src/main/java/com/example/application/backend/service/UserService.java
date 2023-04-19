package com.example.application.backend.service;

import com.example.application.backend.entity.User;
import com.example.application.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Methods called from the MainLayout, RegisterView, and SignInView classes.
 * */

@Service
public class UserService {
    private static UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    /**
     * Saves a user to the database.
     * @param user The user to be stored.
     * */
    public static void saveUserToDatabase(User user) {
        repo.save(user);
    }

    /**
     * Returns a signed-in user.
     * @param sign_in_session_uuid The unique sign in session id corresponding to the user.
     * */
    public static User getUserBySignInSessionUuid(String sign_in_session_uuid) {
        return repo.searchBySignInSessionUuid(sign_in_session_uuid);
    }


    /**
     * Returns a user based on their uuid.
     * @param user_uuid The universally unique identifier of the user to be returned.
     * */
    public static User getUserByUuid(String user_uuid) {
        return repo.searchByUserUuid(user_uuid);
    }

    /**
     * Deletes a user from the database.
     * @param user The user to be deleted.
     * */
    public static void deleteUserFromDatabase(User user) {
        // called when registration is cancelled
        repo.delete(user);
    }
}
