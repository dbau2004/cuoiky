package com.example.coffeeshop.service;

import com.example.coffeeshop.dto.BookingRequestDTO;
import com.example.coffeeshop.dto.BookingResponseDTO;
import com.example.coffeeshop.entity.Booking;
import com.example.coffeeshop.entity.CoffeeTable;
import com.example.coffeeshop.entity.User;
import com.example.coffeeshop.repository.BookingRepository;
import com.example.coffeeshop.repository.TableRepository;
import com.example.coffeeshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TableRepository tableRepository;

    private static final List<String> VALID_STATUSES = List.of("PENDING", "CONFIRMED", "CANCELLED");

    public Booking createBooking(BookingRequestDTO bookingRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("BookingService: User attempting to book: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("BookingService: User role: " + user.getRole());

        // Validate user role
        if (!user.getRole().equals("CUSTOMER")) {
            throw new RuntimeException("Only customers can book tables");
        }

        // Validate table
        CoffeeTable table = tableRepository.findById(bookingRequest.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        // Check table availability
        if (table.getStatus().equals("BOOKED")) {
            throw new RuntimeException("Table is already booked");
        }

        // Check if user already has a booking for this table
        if (bookingRepository.findByUserId(user.getId()).stream()
                .anyMatch(booking -> booking.getTable().getId().equals(table.getId())
                        && !booking.getStatus().equals("CANCELLED"))) {
            throw new RuntimeException("You already have a booking for this table");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTable(table);
        booking.setBookingTime(bookingRequest.getBookingTime());
        booking.setStatus("PENDING");

        // Update table status
        table.setStatus("BOOKED");
        tableRepository.save(table);

        return bookingRepository.save(booking);
    }

    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(booking -> new BookingResponseDTO(
                        booking.getId(),
                        booking.getUser().getId(),
                        booking.getTable().getId(),
                        booking.getTable().getTableNumber(),
                        booking.getBookingTime(),
                        booking.getStatus(),
                        null))
                .collect(Collectors.toList());
    }

    public Booking updateBooking(Long id, String status) {
        if (!VALID_STATUSES.contains(status)) {
            throw new RuntimeException("Invalid status. Must be one of: " + VALID_STATUSES);
        }

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Update table status if booking is cancelled
        if (status.equals("CANCELLED")) {
            CoffeeTable table = booking.getTable();
            table.setStatus("NOT_BOOKED");
            tableRepository.save(table);
        }

        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Update table status
        CoffeeTable table = booking.getTable();
        table.setStatus("NOT_BOOKED");
        tableRepository.save(table);

        bookingRepository.deleteById(id);
    }
}