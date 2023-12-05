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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentLibrarianDetailDto that = (CurrentLibrarianDetailDto) o;

        if (!staffNumber.equals(that.staffNumber)) return false;
        return librarianReservation.equals(that.librarianReservation);
    }

    @Override
    public int hashCode() {
        int result = staffNumber.hashCode();
        result = 31 * result + librarianReservation.hashCode();
        return result;
    }
}