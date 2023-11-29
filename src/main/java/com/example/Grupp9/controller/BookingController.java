package com.example.Grupp9.controller;

import com.example.Grupp9.model.Booking;
import com.example.Grupp9.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingService.findAllBookings();
    }

    @PostMapping("/bookings")
    public Booking newBooking(@RequestBody Booking booking) {
        return bookingService.newBooking(booking);
    }
}
