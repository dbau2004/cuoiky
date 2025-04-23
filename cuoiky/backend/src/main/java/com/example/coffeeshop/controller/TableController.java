package com.example.coffeeshop.controller;

import com.example.coffeeshop.dto.TableRequestDTO;
import com.example.coffeeshop.dto.TableResponseDTO;
import com.example.coffeeshop.entity.CoffeeTable;
import com.example.coffeeshop.service.TableService;

import jakarta.persistence.Table;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {
    @Autowired
    private TableService tableService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TableResponseDTO> createTable(@Valid @RequestBody TableRequestDTO tableRequest) {
        try {
            System.out.println("TableController: Creating table: " + tableRequest.getTableNumber());
            CoffeeTable createdTable = tableService.createTable(tableRequest);
            TableResponseDTO response = new TableResponseDTO(
                createdTable.getId(),
                createdTable.getTableNumber(),
                createdTable.getDescription(),
                createdTable.getStatus(),
                "Table created successfully"
            );
            System.out.println("TableController: CoffeeTable created: " + createdTable.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            TableResponseDTO errorResponse = new TableResponseDTO(null, 0, null, null, e.getMessage());
            if (e.getMessage().contains("already exists")) {
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CoffeeTable> getAllTables() {
        return tableService.getAllTables();
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<CoffeeTable>> getAvailableTables() {
        return ResponseEntity.ok(tableService.getAvailableTables());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CoffeeTable updateTable(@PathVariable Long id, @RequestBody CoffeeTable table) {
        return tableService.updateTable(id, table);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
    }
}