package com.example.coffeeshop.controller;

import com.example.coffeeshop.dto.BookingRequestDTO;
import com.example.coffeeshop.dto.BookingResponseDTO;
import com.example.coffeeshop.entity.Booking;
import com.example.coffeeshop.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequestDTO bookingRequest) {
        try {
            Booking booking = bookingService.createBooking(bookingRequest);
            BookingResponseDTO response = new BookingResponseDTO(
                    booking.getId(),
                    booking.getUser().getId(),
                    booking.getTable().getId(),
                    booking.getTable().getTableNumber(),
                    booking.getBookingTime(),
                    booking.getStatus(),
                    "Booking created successfully"
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            BookingResponseDTO errorResponse = new BookingResponseDTO(null, null, null, 0, null, null, e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingResponseDTO> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponseDTO> updateBooking(@PathVariable Long id, @RequestBody String status) {
        try {
            Booking updatedBooking = bookingService.updateBooking(id, status);
            BookingResponseDTO response = new BookingResponseDTO(
                    updatedBooking.getId(),
                    updatedBooking.getUser().getId(),
                    updatedBooking.getTable().getId(),
                    updatedBooking.getTable().getTableNumber(),
                    updatedBooking.getBookingTime(),
                    updatedBooking.getStatus(),
                    "Booking updated successfully"
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            BookingResponseDTO errorResponse = new BookingResponseDTO(null, null, null, 0, null, null, e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponseDTO> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            BookingResponseDTO response = new BookingResponseDTO(null, null, null, 0, null, null, "Booking deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            BookingResponseDTO errorResponse = new BookingResponseDTO(null, null, null, 0, null, null, e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}