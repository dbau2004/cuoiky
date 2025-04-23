package com.example.coffeeshop.dto;

import java.time.LocalDateTime;

public class BookingResponseDTO {
    private Long id;
    private Long userId;
    private Long tableId;
    private int tableNumber;
    private LocalDateTime bookingTime;
    private String status;
    private String message;

    // Constructor
    public BookingResponseDTO(Long id, Long userId, Long tableId, int tableNumber, LocalDateTime bookingTime, String status, String message) {
        this.id = id;
        this.userId = userId;
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.bookingTime = bookingTime;
        this.status = status;
        this.message = message;
    }

    // Getters, setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }
    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}