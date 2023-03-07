package com.example.application.backend.service;

import com.example.application.backend.entity.User;
import com.example.application.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public static void saveUserToDatabase(User user) {
        repo.save(user);
    }

    public static User getUserBySignInSessionUuid(String sign_in_session_uuid) {
        return repo.search(sign_in_session_uuid);
    }
}
