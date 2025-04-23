package com.example.coffeeshop.repository;

import com.example.coffeeshop.entity.CoffeeTable; // Sửa từ Table thành CoffeeTable

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<CoffeeTable, Long> {
    List<CoffeeTable> findByStatus(String status);
}