package com.vimol.issuetracker.repositories;

import com.vimol.issuetracker.entities.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<SubCategory, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM sub_category WHERE category_id = :id")
    List<SubCategory> subs(Long id);
}
