package com.example.application.backend.repository;

import com.example.application.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Custom queries called from the UserService class.
 * */

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // see Vaadin docs: Implementing Filtering in the Repository
    // see query syntax: Jakarta Persistence Query Language

    @Query(value = "SELECT u FROM User u WHERE u.sign_in_session_uuid = :searchTerm")
    User searchBySignInSessionUuid(@Param("searchTerm") String searchTerm);

    @Query(value = "SELECT u FROM User u WHERE u.user_uuid = :searchTerm")
    User searchByUserUuid(@Param("searchTerm") String searchTerm);
}
