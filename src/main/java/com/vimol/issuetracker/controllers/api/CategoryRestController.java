package com.vimol.issuetracker.controllers.api;

import com.vimol.issuetracker.entities.SubCategory;
import com.vimol.issuetracker.repositories.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryRestController {

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @GetMapping("/subCategories")
    public List<SubCategory> getSubs(@RequestParam("catId") Long id){
        return subcategoryRepository.subs(id);
    }
}
