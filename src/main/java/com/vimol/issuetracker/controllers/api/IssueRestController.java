package com.vimol.issuetracker.controllers.api;

import com.vimol.issuetracker.entities.Issue;
import com.vimol.issuetracker.repositories.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class IssueRestController {

    @Autowired
    private IssueRepository issueRepository;

    @PostMapping("/createIssue")
    public Object createIssue(@RequestBody Issue issue){
        return issueRepository.save(issue);
    }

    @PostMapping("/updateIssue")
    public Object updateIssue(@RequestBody Issue issue){
        Optional<Issue> issue1 = issueRepository.findById(issue.getId());
        if(issue1.isPresent()){
            issue1.get().setStatus(issue.getStatus());
            issue1.get().setSolution(issue.getSolution());

            return issueRepository.save(issue1.get());
        }
        return new Issue();
    }

}
