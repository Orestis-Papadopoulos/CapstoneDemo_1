package com.example.application.backend.repository;

import com.example.application.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.io.FilenameUtils.concat;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // see Vaadin docs: Implementing Filtering in the Repository
    // see query syntax: Jakarta Persistence Query Language

    @Query(value = "SELECT u FROM User u WHERE u.sign_in_session_uuid = :searchTerm")
    User search(@Param("searchTerm") String searchTerm);
}
