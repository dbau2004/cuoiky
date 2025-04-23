package com.example.coffeeshop.dto;

public class TableResponseDTO {
    private Long id;
    private int tableNumber;
    private String description;
    private String status;
    private String message;

    // Constructor
    public TableResponseDTO(Long id, int tableNumber, String description, String status, String message) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.description = description;
        this.status = status;
        this.message = message;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}