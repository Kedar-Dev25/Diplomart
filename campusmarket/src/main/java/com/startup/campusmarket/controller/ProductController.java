package com.startup.campusmarket.controller;

import com.startup.campusmarket.model.Product;
import com.startup.campusmarket.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile; // Important!

// File Handling ke liye ye imports zaroori hain
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/add")
    public String addProduct(Product product, 
                             @RequestParam("imageFile") MultipartFile imageFile, 
                             HttpSession session) {
        
        String sellerEmail = (String) session.getAttribute("loggedInUserEmail");
        String sellerName = (String) session.getAttribute("loggedInUserName");

        if (sellerEmail == null) return "redirect:/register";

        try {
            if (!imageFile.isEmpty()) {
                // Folder path: static/images/ ke andar save hoga
                String uploadDir = "src/main/resources/static/images/";
                
                // Ek unique file name banao taaki same name ki files overwrite na ho
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);
                
                // Agar images folder nahi bana hua, toh ye bana dega
                if (!Files.exists(Paths.get(uploadDir))) {
                    Files.createDirectories(Paths.get(uploadDir));
                }

                // File save karo
                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                
                // DB mein path set karo (Thymeleaf ke liye)
                product.setImagePath("/images/" + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Error handling ke liye yahan logic daal sakte ho
        }

        product.setSellerEmail(sellerEmail);
        product.setSellerName(sellerName);
        product.setStatus("AVAILABLE");
        productRepository.save(product);

        return "redirect:/dashboardSeller";
    }




    @PostMapping("/buy")
    public String buyProduct(@RequestParam Long productId, HttpSession session){
        String buyerEmail = (String) session.getAttribute("loggedInUserEmail");
        String buyerName = (String) session.getAttribute("loggedInUserName");
        String branch = (String) session.getAttribute("loggedInUserBranch");
        int yr = (Integer) session.getAttribute("loggedInUserYr");

        if (buyerEmail == null || buyerName == null ) return "redirect:/register";

        Product product = productRepository.findById(productId).orElse(null);
        
        if (product != null) {

            if(product.getSellerEmail().equals(buyerEmail)) return "redirect:/dashboardBuyer";

            String myAdminEmail = "kedar.code7@gmail.com"; 
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(myAdminEmail);
            message.setSubject("🔥 New Deal: " + product.getName());
            message.setText("Bhai, Order aya hai\n\n" +
                          "Item: " + product.getName() + "\n" +
                          "Price: ₹" + product.getPrice() + "\n" +
                          "Seller: " + product.getSellerName() + " (" + product.getSellerEmail() + ")\n" +
                          "Seller From: " + yr + " year and " + branch + " Branch\n" +
                          "Buyer: " + buyerName + " (" + buyerEmail + ")");
            
            mailSender.send(message);

            product.setStatus("PENDING_ADMIN"); 
            productRepository.save(product);
        }

        return "redirect:/dashboardBuyer";
    }

    @PostMapping("/product/delete")
    public String deleteProduct(@RequestParam Long id) {
        productRepository.deleteById(id);
        return "redirect:/dashboardSeller";
    }    
}
    
