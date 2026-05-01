package com.lee.stayhub.model;

import java.time.LocalDate;


public record BookingRequest(
        long listingId,
        LocalDate checkInDate,
        LocalDate checkOutDate
) {
}
