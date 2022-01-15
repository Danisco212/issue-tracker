package com.vimol.issuetracker.controllers;

import com.vimol.issuetracker.entities.Category;
import com.vimol.issuetracker.entities.Issue;
import com.vimol.issuetracker.entities.User;
import com.vimol.issuetracker.repositories.CategoryRepository;
import com.vimol.issuetracker.repositories.IssueRepository;
import com.vimol.issuetracker.services.AuthService;
import com.vimol.issuetracker.utils.JwtTokenProvider;
import com.vimol.issuetracker.utils.SecurityCipher;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IssueController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User isLoggedIn(String authToken) {
        if (authToken != null && !authToken.equals("")) {
            // token exists
            String decrypt = SecurityCipher.decrypt(authToken);
            boolean authCorrect = tokenProvider.validateToken(decrypt);
            if (authCorrect) {
                try {
                    ResponseEntity<User> mUser = authService.loginUser(tokenProvider.getUsername(decrypt));
                    if (mUser != null) {
                        return mUser.getBody();
                    }
                } catch (ExpiredJwtException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @GetMapping("/createIssue")
    public Object createIssue(@CookieValue(required = false, name = "Issue_AuthToken") String authToken, Model model) {
        User user = isLoggedIn(authToken);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("issue", new Issue());
            model.addAttribute("categories", categoryRepository.findAll());
            return "create-issue";
        }
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/issue")
    public Object issueDetails(@CookieValue(required = false, name = "Issue_AuthToken") String authToken, @RequestParam("id") Long id, Model model) {
        User user = isLoggedIn(authToken);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("issue", issueRepository.findById(id));
            model.addAttribute("categories", categoryRepository.findAll());
            return "issue";
        }
        return new ModelAndView("redirect:/login");
    }

}
