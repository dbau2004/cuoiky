package com.example.coffeeshop.dto;

import java.time.LocalDateTime;

public class BookingRequestDTO {
    private Long tableId;
    private LocalDateTime bookingTime;

    // Getters, setters
    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }
}