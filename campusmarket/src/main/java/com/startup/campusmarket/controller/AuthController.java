package com.startup.campusmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import com.startup.campusmarket.model.User;
import com.startup.campusmarket.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String registerUser(User user) {
        userRepository.save(user);

        return "redirect:/login";
    }

    
    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session) {   // ✅ session added

        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            // ✅ SAME key as PageController
            session.setAttribute("loggedInUserEmail", user.getEmail());
            session.setAttribute("loggedInUserName", user.getName());
            session.setAttribute("loggedInUserBranch", user.getBranch());
            session.setAttribute("loggedInUserYr",user.getYear());
            System.out.println("Seller Email " + user.getEmail());
            return "redirect:/dashboardBuyer";
        } else {
            return "redirect:/login";
        }
    }


    
    @PostMapping("/logout")
    public String logoutUser(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }

   
}