package com.vimol.issuetracker.controllers.api;

import com.vimol.issuetracker.entities.Issue;
import com.vimol.issuetracker.repositories.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssueRestController {

    @Autowired
    private IssueRepository issueRepository;

    @PostMapping("/createIssue")
    public Object createIssue(@RequestBody Issue issue){
        return issueRepository.save(issue);
    }
}
