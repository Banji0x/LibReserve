package dev.banji.LibReserve.model.dtos;

import java.util.List;

public record LibrarianSeatDto(Boolean reserveLibrarianSeat, Long numberOfLibrarians, List<Long> seatNumbers) {
}
