package com.vimol.issuetracker.controllers;

import com.vimol.issuetracker.dto.Token;
import com.vimol.issuetracker.entities.User;
import com.vimol.issuetracker.repositories.UserRepository;
import com.vimol.issuetracker.services.AuthService;
import com.vimol.issuetracker.utils.JwtTokenProvider;
import com.vimol.issuetracker.utils.SecurityCipher;
import com.vimol.issuetracker.utils.UserPrincipal;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

    private boolean isLoggedIn(String authToken){
        if(authToken != null && !authToken.equals("")){
            // token exists
            String decrypt = SecurityCipher.decrypt(authToken);
            boolean authCorrect = tokenProvider.validateToken(decrypt);
            if(authCorrect){
                try {
                    ResponseEntity<User> mUser = authService.loginUser(tokenProvider.getUsername(decrypt));
                    if(mUser != null){
                        return true;
                    }
                }catch (ExpiredJwtException e){
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @GetMapping("/login")
    public Object loginUser(@CookieValue(required = false, name = "Issue_AuthToken") String authToken){
        if(isLoggedIn(authToken)){
            return new ModelAndView("redirect:/");
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", new User());
        modelAndView.setViewName("login.html");
        return modelAndView;

    }

    @PostMapping("/login")
    public Object greetingSubmit(@ModelAttribute("user") User user, Model model, HttpServletResponse response) {
        if(user.getEmail().isEmpty() || user.getPassword().isEmpty()){
            user.setLoginError("Invalid username or password");
            return user.getLoginError();
        }
        ResponseEntity<User> mUser = authService.loginUser(user.getEmail(), user.getPassword());
        if(mUser == null){
            user.setLoginError("Invalid username or password");
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("login.html");
            modelAndView.addObject("user", user);
            return modelAndView;
        }
        Token mToken = tokenProvider.generateToken(mUser.getBody());
        String encryptedToken = SecurityCipher.encrypt(mToken.getTokenValue());
        response.addCookie(new Cookie("Issue_AuthToken", encryptedToken));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }


    @GetMapping("/register")
    public Object registerUser(@CookieValue(required = false, name = "Issue_AuthToken") String authToken){
        if(isLoggedIn(authToken)){
            return new ModelAndView("redirect:/");
        }else{
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("register.html");
            return modelAndView;
        }
    }

    private ModelAndView registerError(User user){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register.html");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/register")
    public Object registerUser(@ModelAttribute("user") User user, Model model, HttpServletResponse response){
        // check if all fields are filled
        if(user.getPassword().isEmpty() || user.getPhoneNumber().isEmpty() || user.getEmail().isEmpty() || user.getFirstName().isEmpty() || user.getLastName().isEmpty()){
            // empty field
            user.setLoginError("Please fill all fields marked with *");
            return registerError(user);
        }
        // check password length
        if (user.getPassword().length() <6){
            user.setLoginError("Password must be at least 6 characters");
            return registerError(user);
        }
        // check if there is already a user with that email
        Optional<User> existUser = userRepository.findByEmail(user.getEmail());
        if(existUser.isPresent()){
            // email taken
            user.setLoginError("This email is already taken by another user");
            return registerError(user);
        }
        // check if the user has the phone number
        existUser = userRepository.findByPhoneNumber(user.getPhoneNumber());
        if(existUser.isPresent()){
            // number is taken
            user.setLoginError("This number is already taken by another user");
            return registerError(user);
        }

        // save the user
        userRepository.save(user);
        // login the user
        ResponseEntity<User> registeredUser = authService.loginUser(user.getEmail(), user.getPassword());
        // save the token
        Token mToken = tokenProvider.generateToken(registeredUser.getBody());
        String encryptedToken = SecurityCipher.encrypt(mToken.getTokenValue());
        response.addCookie(new Cookie("Issue_AuthToken", encryptedToken));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }
}
