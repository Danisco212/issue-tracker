package com.vimol.issuetracker.repositories;

import com.vimol.issuetracker.entities.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("SELECT o FROM Issue o WHERE user_id = :id")
    List<Issue> myCreatedIssues(Long id);

    @Query("SELECT o FROM Issue o WHERE solver_id = :id")
    List<Issue> myAssignedIssues(Long id);
}
