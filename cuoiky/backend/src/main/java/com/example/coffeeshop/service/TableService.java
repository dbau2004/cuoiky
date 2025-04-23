package com.example.coffeeshop.service;

import com.example.coffeeshop.dto.TableRequestDTO;
import com.example.coffeeshop.entity.CoffeeTable;
import com.example.coffeeshop.repository.TableRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {
    @Autowired
    private TableRepository tableRepository;

    private static final List<String> VALID_STATUSES = List.of("NOT_BOOKED", "BOOKED");

    public CoffeeTable createTable(TableRequestDTO tableRequest) {
        System.out.println("TableService: Creating table: " + tableRequest.getTableNumber());

        // Validation
        if (!VALID_STATUSES.contains(tableRequest.getStatus())) {
            throw new RuntimeException("Invalid status. Must be one of: " + VALID_STATUSES);
        }

        // Check for duplicate table number
        if (tableRepository.findAll().stream().anyMatch(table -> table.getTableNumber() == tableRequest.getTableNumber())) {
            throw new RuntimeException("Table number " + tableRequest.getTableNumber() + " already exists");
        }

        // Map DTO to entity
        CoffeeTable table = new CoffeeTable();
        table.setTableNumber(tableRequest.getTableNumber());
        table.setDescription(tableRequest.getDescription());
        table.setStatus(tableRequest.getStatus());

        // Save table
        CoffeeTable savedTable = tableRepository.save(table);
        System.out.println("TableService: CoffeeTable created with ID: " + savedTable.getId());
        return savedTable;
    }

    public List<CoffeeTable> getAllTables() {
        return tableRepository.findAll();
    }

    public List<CoffeeTable> getAvailableTables() {
        return tableRepository.findByStatus("NOT_BOOKED");
    }

    public CoffeeTable updateTable(Long id, CoffeeTable table) {
        CoffeeTable existingTable = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));
        existingTable.setTableNumber(table.getTableNumber());
        existingTable.setDescription(table.getDescription());
        existingTable.setStatus(table.getStatus());
        return tableRepository.save(existingTable);
    }

    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }
}