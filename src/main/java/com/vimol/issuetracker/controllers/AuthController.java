package com.vimol.issuetracker.controllers;

import com.vimol.issuetracker.entities.User;
import com.vimol.issuetracker.repositories.UserRepository;
import com.vimol.issuetracker.services.AuthService;
import com.vimol.issuetracker.utils.JwtTokenProvider;
import com.vimol.issuetracker.utils.SecurityCipher;
import com.vimol.issuetracker.utils.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @GetMapping("/login")
    public Object loginUser(@CookieValue(required = false, name = "Issue_AuthToken") String authToken){
        if(false){
            // token exists
            String decrypt = SecurityCipher.decrypt(authToken);
            boolean authCorrect = tokenProvider.validateToken(decrypt);
            if(authCorrect){
                ResponseEntity<User> mUser = authService.loginUser(tokenProvider.getUsername(decrypt));
                if(mUser != null){
                    return mUser;
                }
                return tokenProvider.getUsername(authToken);
            }
            return "incorrect token - " + decrypt;
        }else{
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("login.html");
            return modelAndView;
        }
    }

    @PostMapping("/login")
    public Object greetingSubmit(@ModelAttribute("user") User user, Model model) {
        if(user.getEmail().isEmpty() || user.getPassword().isEmpty()){
            user.setLoginError("Invalid username or password");
            return user.getLoginError();
        }

        ResponseEntity<User> mUser = authService.loginUser(user.getEmail(), user.getPassword());

        if(mUser == null){
            user.setLoginError("Invalid username or password");
            return passwordEncoder.encode(user.getPassword());
        }
        return mUser;
    }

    @RequestMapping("/logout")
    public Object logoutUser(){
        ModelAndView modelAndView = new ModelAndView("redirect:/register");
        authService.logoutUser();
        return "modelAndView";
//        return authService.logoutUser();
    }

    @GetMapping("/register")
    public Object registerUser(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", new User());
        modelAndView.setViewName("register.html");
        return modelAndView;
    }
}
