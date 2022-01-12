package com.vimol.issuetracker.services.impl;

import com.vimol.issuetracker.dto.Token;
import com.vimol.issuetracker.entities.User;
import com.vimol.issuetracker.repositories.UserRepository;
import com.vimol.issuetracker.services.AuthService;
import com.vimol.issuetracker.utils.CookieUtil;
import com.vimol.issuetracker.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CookieUtil cookieUtil;

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmailAndPassword(String email, String password) {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<User> loginUser(String email, String password) {
        Optional<User> user = userRepository.findByLoginInfo(email, password);

        if(user.isPresent()){
            HttpHeaders responseHeaders = new HttpHeaders();
            Token newAccessToken;
            newAccessToken = tokenProvider.generateToken(user.get());
            addAccessTokenCookie(responseHeaders, newAccessToken);
            return ResponseEntity.ok().headers(responseHeaders).body(user.get());
        }else{
            return null;
        }
    }

    @Override
    public ResponseEntity<Object> logoutUser() {
        HttpHeaders headers = new HttpHeaders();
        Token emptyToken = tokenProvider.removeToken();
        addAccessTokenCookie(headers, emptyToken);
        return ResponseEntity.ok().headers(headers).body(null);
    }

    @Override
    public ResponseEntity<User> loginUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isPresent()){
            HttpHeaders responseHeaders = new HttpHeaders();
            Token newAccessToken;
            newAccessToken = tokenProvider.generateToken(user.get());
            addAccessTokenCookie(responseHeaders, newAccessToken);
            return ResponseEntity.ok().headers(responseHeaders).body(user.get());
        }else{
            return null;
        }
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
                cookieUtil.createAccessTokenCookie(token.getTokenValue(), token.getDuration()).toString());
    }
}
