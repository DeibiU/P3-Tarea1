package com.project.demo.logic.entity.category;

import com.project.demo.logic.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.name = ?1")
    List<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE %?1%")
    List<Category> findCategoriesWithCharacterInName(String character);

}
