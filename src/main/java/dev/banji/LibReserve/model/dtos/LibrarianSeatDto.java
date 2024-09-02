package dev.banji.LibReserve.model.dtos;

import java.util.Set;

public record LibrarianSeatDto(Boolean reserveLibrarianSeat, Long numberOfLibrarians, Set<Long> seatNumbers) {
}