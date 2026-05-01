package com.lee.stayhub.model;

public record ErrorResponse(
        String message,
        String error
) {
}