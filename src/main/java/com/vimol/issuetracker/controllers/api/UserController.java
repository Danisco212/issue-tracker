package com.vimol.issuetracker.controllers.api;

import com.vimol.issuetracker.entities.User;
import com.vimol.issuetracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getUser")
    public List<User> getUser(@RequestParam("name") String name){
        List<User> mUserList = new ArrayList<>();
        mUserList = userRepository.findByName(name);
        return mUserList;
    }
}
