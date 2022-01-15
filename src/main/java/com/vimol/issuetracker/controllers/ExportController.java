package com.vimol.issuetracker.controllers;

import com.vimol.issuetracker.entities.Issue;
import com.vimol.issuetracker.entities.User;
import com.vimol.issuetracker.repositories.CategoryRepository;
import com.vimol.issuetracker.repositories.IssueRepository;
import com.vimol.issuetracker.repositories.UserRepository;
import com.vimol.issuetracker.services.AuthService;
import com.vimol.issuetracker.utils.JwtTokenProvider;
import com.vimol.issuetracker.utils.SecurityCipher;
import com.vimol.issuetracker.utils.UserExcelExporter;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ExportController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

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
                } catch (ExpiredJwtException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @GetMapping("/export")
    public Object exportData(@RequestParam(defaultValue = "all",name = "type") String status, @CookieValue(required = false, name = "Issue_AuthToken") String authToken, Model model){
        User user = isLoggedIn(authToken);
        if (user != null) {
            List<Issue> issues = issueRepository.myAssignedIssues(user.get_id());
            if(!status.equals("all")){
                issues = issues.stream().filter(issue -> issue.getStatus().equals(stringToStatus(status))).collect(Collectors.toList());
            }else{
                issues = issueRepository.myAssignedIssues(user.get_id());
            }
            issues.forEach(issue -> {
                issue.setCategory(categoryRepository.findById(issue.getCategoryId()).get());
                issue.setUser(userRepository.findById(issue.getUserId()).get());
                issue.setSolver(userRepository.findById(issue.getSolverId()).get());
            });
            model.addAttribute("issues", issues);
            model.addAttribute("user", user);
            model.addAttribute("type", status);
            return "export";
        }
        return new ModelAndView("redirect:/login");
    }

    private Issue.IssueStatus stringToStatus (String status){
        switch (status){
            case "resolved":
                return Issue.IssueStatus.RESOLVED;
            case "received":
                return Issue.IssueStatus.RECEIVED;
            case "resolving":
                return Issue.IssueStatus.RESOLVING;
            case "pending":
                return Issue.IssueStatus.PENDING;
            default:
                return Issue.IssueStatus.RESOLVING;
        }
    }

    @GetMapping("/export/excel")
    public void exportToExcel(@RequestParam(defaultValue = "all",name = "type") String status, @CookieValue(required = false, name = "Issue_AuthToken") String authToken, HttpServletResponse response) throws IOException {
        User user = isLoggedIn(authToken);
        if (user != null) {
            response.setContentType("application/octet-stream");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());

            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=issues_" + currentDateTime + ".xlsx";
            response.setHeader(headerKey, headerValue);

            List<Issue> issues = issueRepository.myAssignedIssues(user.get_id());
            if(!status.equals("all")){
                issues = issues.stream().filter(issue -> issue.getStatus().equals(stringToStatus(status))).collect(Collectors.toList());
            }else{
                issues = issueRepository.myAssignedIssues(user.get_id());
            }
            issues.forEach(issue -> {
                issue.setCategory(categoryRepository.findById(issue.getCategoryId()).get());
                issue.setUser(userRepository.findById(issue.getUserId()).get());
                issue.setSolver(userRepository.findById(issue.getSolverId()).get());
            });
            UserExcelExporter excelExporter = new UserExcelExporter(issues);

            excelExporter.export(response);
        }
    }
}
