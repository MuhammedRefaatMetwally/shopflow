package com.shopflow.repository;

import com.shopflow.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    Optional<Product> findByIdAndActiveTrue(Long id);

    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
        AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    List<Product> searchByName(@Param("name") String name);
}