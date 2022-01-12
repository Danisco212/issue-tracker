package com.vimol.issuetracker.services;

import com.vimol.issuetracker.entities.User;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface AuthService{

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByEmailAndPassword(String email, String password);

    ResponseEntity<User> loginUser(String email, String password);

    ResponseEntity<User> loginUser(String email);

    ResponseEntity<Object> logoutUser();
}
