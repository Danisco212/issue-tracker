package com.vimol.issuetracker.repositories;

import com.vimol.issuetracker.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {


}
