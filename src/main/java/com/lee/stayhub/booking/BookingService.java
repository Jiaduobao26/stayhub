package com.lee.stayhub.booking;
import com.lee.stayhub.model.BookingDto;
import com.lee.stayhub.model.BookingEntity;
import com.lee.stayhub.model.ListingEntity;
import com.lee.stayhub.repository.BookingRepository;
import com.lee.stayhub.repository.ListingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {


    private final BookingRepository bookingRepository;
    private final ListingRepository listingRepository;


    public BookingService(
            BookingRepository bookingRepository,
            ListingRepository listingRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.listingRepository = listingRepository;
    }


    public List<BookingDto> findBookingsByGuestId(long guestId) {
        return bookingRepository.findAllByGuestId(guestId)
                .stream()
                .map(BookingDto::new)
                .toList();
    }


    public List<BookingDto> findBookingsByListingId(long hostId, long listingId) {
        ListingEntity listing = listingRepository.getReferenceById(listingId);
        if (listing.getHostId() != hostId) {
            throw new ListingBookingsNotAllowedException(hostId, listingId);
        }
        return bookingRepository.findAllByListingId(listingId)
                .stream()
                .map(BookingDto::new)
                .toList();
    }


    public void createBooking(long guestId, long listingId, LocalDate checkIn, LocalDate checkOut) {
        if (!listingRepository.existsById(listingId)) {
            throw new EntityNotFoundException(String.format("Listing %d not found", listingId));
        }
        if (checkIn.isAfter(checkOut)) {
            throw new InvalidBookingException("Check-in date must be before check-out date.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidBookingException("Check-in date must be in the future.");
        }
        List<BookingEntity> overlappedBookings = bookingRepository.findOverlappedBookings(listingId, checkIn, checkOut);
        if (!overlappedBookings.isEmpty()) {
            throw new InvalidBookingException("Booking dates conflict, please select different dates.");
        }
        bookingRepository.save(new BookingEntity(null, guestId, listingId, checkIn, checkOut));
    }


    public void deleteBooking(long guestId, long bookingId) {
        BookingEntity booking = bookingRepository.getReferenceById(bookingId);
        if (booking.getGuestId() != guestId) {
            throw new DeleteBookingNotAllowedException(guestId, bookingId);
        }
        bookingRepository.deleteById(bookingId);
    }


    public boolean existsActiveBookings(long listingId) {
        return bookingRepository.existsByListingIdAndCheckOutDateAfter(listingId, LocalDate.now());
    }
}
