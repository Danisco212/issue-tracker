package com.vimol.issuetracker.controllers;

import com.vimol.issuetracker.entities.Category;
import com.vimol.issuetracker.entities.SubCategory;
import com.vimol.issuetracker.entities.User;
import com.vimol.issuetracker.repositories.CategoryRepository;
import com.vimol.issuetracker.repositories.SubcategoryRepository;
import com.vimol.issuetracker.services.AuthService;
import com.vimol.issuetracker.utils.JwtTokenProvider;
import com.vimol.issuetracker.utils.SecurityCipher;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class CategoryController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    private User isLoggedIn(String authToken) {
        if (authToken != null && !authToken.equals("")) {
            // token exists
            String decrypt = SecurityCipher.decrypt(authToken);
            boolean authCorrect = tokenProvider.validateToken(decrypt);
            if (authCorrect) {
                try{
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

    @GetMapping("/addCategory")
    public Object categories(@CookieValue(required = false, name = "Issue_AuthToken") String authToken, Model model) {
        User user = isLoggedIn(authToken);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("category", new Category());
            return "add-category";
        }
        return new ModelAndView("redirect:/login");
    }

    @PostMapping("/addCategory")
    public Object addCategory(@ModelAttribute("category") Category category, @CookieValue(required = false, name = "Issue_AuthToken") String authToken){
        // check the fields
        if(category.getCatName().isEmpty() || category.getCatDescription().isEmpty()){
            // error
            category.setCategoryError("Please fill all fields");
            ModelAndView modelAndView =new ModelAndView();
            modelAndView.addObject("category", category);
            modelAndView.addObject("user", isLoggedIn(authToken));
            modelAndView.setViewName("add-category");
            return modelAndView;
        }

        Category newCat = categoryRepository.save(category);
        ModelAndView modelAndView =new ModelAndView();
        modelAndView.setViewName("redirect:/category?id=" + newCat.getId());
        return modelAndView;
    }

    @GetMapping("/category")
    public Object getCategory(@CookieValue(required = false, name = "Issue_AuthToken") String authToken, @RequestParam("id") Long id, Model model){
        User user = isLoggedIn(authToken);
        if (user != null) {
            model.addAttribute("user", user);
            Optional<Category> category = categoryRepository.findById(id);
            if(category.isPresent()){
                model.addAttribute("category", category.get());
                model.addAttribute("sub_category", new SubCategory());
                return "category";
            }
        }
        return new ModelAndView("redirect:/login");
    }

    @PostMapping("/add-subcat/{id}")
    public Object addSubCat(@ModelAttribute SubCategory category, @CookieValue(required = false, name = "Issue_AuthToken") String authToken, @PathVariable("id") Long id){
        if(!category.getCatName().isEmpty()){
            // error
            SubCategory newSub = new SubCategory();
            newSub.setCategory(categoryRepository.findById(id).get());
            newSub.setCatName(category.getCatName());
            subcategoryRepository.save(newSub);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/category?id="+id);
        modelAndView.addObject("sub_category", new SubCategory());
        modelAndView.addObject("category", categoryRepository.findById(id).get());
        modelAndView.addObject("user", isLoggedIn(authToken));

        return modelAndView;
    }
}
