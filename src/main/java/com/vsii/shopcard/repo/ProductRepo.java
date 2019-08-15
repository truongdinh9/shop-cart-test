package com.vsii.shopcard.repo;

import com.vsii.shopcard.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
}
