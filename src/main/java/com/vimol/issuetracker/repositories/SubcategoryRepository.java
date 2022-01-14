package com.vimol.issuetracker.repositories;

import com.vimol.issuetracker.entities.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubcategoryRepository extends JpaRepository<SubCategory, Long> {

}
