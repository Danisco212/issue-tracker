package com.vimol.issuetracker.repositories;

import com.vimol.issuetracker.entities.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}
