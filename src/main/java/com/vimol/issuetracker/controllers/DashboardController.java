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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IssueRepository issueRepository;

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
                }catch (ExpiredJwtException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @GetMapping("/")
    public Object homePage(@CookieValue(required = false, name = "Issue_AuthToken") String authToken, Model model) {
        User user = isLoggedIn(authToken);
        if (user != null) {
            // get the user issues
            List<Issue> issues = issueRepository.myCreatedIssues(user.get_id());
            if(user.getPosition().equals(User.UserType.SOLVER)){
                issues = issueRepository.myAssignedIssues(user.get_id());
            }
            model.addAttribute("pending", issues.stream().filter(issue -> issue.getStatus().equals(Issue.IssueStatus.PENDING)).collect(Collectors.toList()));
            model.addAttribute("resolved", issues.stream().filter(issue -> issue.getStatus().equals(Issue.IssueStatus.RESOLVED)).collect(Collectors.toList()));
            model.addAttribute("resolving", issues.stream().filter(issue -> issue.getStatus().equals(Issue.IssueStatus.RESOLVING)).collect(Collectors.toList()));
            model.addAttribute("received", issues.stream().filter(issue -> issue.getStatus().equals(Issue.IssueStatus.RECEIVED)).collect(Collectors.toList()));
            model.addAttribute("user", user);
            return "index";
        }
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/categories")
    public Object categories(@CookieValue(required = false, name = "Issue_AuthToken") String authToken, Model model) {
        User user = isLoggedIn(authToken);
        if (user != null) {
            // get the categories
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("user", user);
            model.addAttribute("categories", categories);
            return "categories";
        }
        return new ModelAndView("redirect:/login");
    }
}
