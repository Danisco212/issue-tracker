package com.vimol.issuetracker.pages;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HttpServletBean;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LandingPage {
    @GetMapping("/")
    public Object landingPage(@CookieValue(required = false, name = "foo") String fooCookie, HttpServletResponse response){
        if(fooCookie == null){
            response.addCookie(new Cookie("foo","somefooooo"));
            return "no cookie";
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", fooCookie);
        modelAndView.setViewName("login.html");
        return modelAndView;
//        return "Hello to my page";
    }
}
