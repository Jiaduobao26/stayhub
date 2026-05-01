package com.lee.stayhub.booking;

import com.lee.stayhub.model.BookingDto;
import com.lee.stayhub.model.BookingRequest;
import com.lee.stayhub.model.UserEntity;
import com.lee.stayhub.model.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {


    private final BookingService bookingService;


    private final UserEntity user = new UserEntity(4L, "lively_wanderlust", "1z3dUW", UserRole.ROLE_GUEST);


    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @GetMapping
    public List<BookingDto> getGuestBookings() {
        return bookingService.findBookingsByGuestId(user.getId());
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBooking(@RequestBody BookingRequest body) {
        bookingService.createBooking(user.getId(), body.listingId(), body.checkInDate(), body.checkOutDate());
    }


    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable long bookingId) {
        bookingService.deleteBooking(user.getId(), bookingId);
    }
}

