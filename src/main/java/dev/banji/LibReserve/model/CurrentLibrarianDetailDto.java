package dev.banji.LibReserve.model;

public record CurrentLibrarianDetailDto(String staffNumber,
                                        LibrarianReservation librarianReservation) implements InmemoryUserDetailDto {
    @Override
    public Long getSeatNumber() {
        return librarianReservation.seatNumber;
    }

    @Override
    public String getIdentifier() {
        return staffNumber;
    }

    @Override
    public Reservation getReservation() {
        return librarianReservation;
    }

}