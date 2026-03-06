package com.startup.campusmarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

import com.startup.campusmarket.model.Product;
import com.startup.campusmarket.repository.ProductRepository;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    private ProductRepository productRepository; // ✅ Isse data aayega

    @GetMapping("/")
public String Home(Model model) {
        // Buyer dashboard pe saare available products dikhao
        List<Product> allProducts = productRepository.findAll();
        model.addAttribute("availableProducts", allProducts);
        return "dashboard-buyer";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    @GetMapping("/dashboardBuyer")
    public String dashboardBuyer(Model model) {
        // Buyer dashboard pe saare available products dikhao
        List<Product> allProducts = productRepository.findAll();
        model.addAttribute("availableProducts", allProducts);
        return "dashboard-buyer";
    }

    @GetMapping("/dashboardSeller")
public String dashboardSeller(HttpSession session, Model model) {

    String sellerEmail = (String) session.getAttribute("loggedInUserEmail");

    // ✅ agar login nahi hai → pehle register dikhao
    if (sellerEmail == null) {
        return "redirect:/register";
    }

    // ✅ login hai → apne products dikhao
    List<Product> myProducts = productRepository.findBySellerEmail(sellerEmail);
    model.addAttribute("sellerProducts", myProducts);

    return "dashboard-seller";
}

}