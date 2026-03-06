package com.startup.campusmarket.repository;

import com.startup.campusmarket.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(String status);
    List<Product> findBySellerEmail(String email);
}