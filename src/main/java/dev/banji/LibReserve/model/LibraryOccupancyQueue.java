package dev.banji.LibReserve.model;

import dev.banji.LibReserve.exceptions.LibraryClosedException;
import dev.banji.LibReserve.exceptions.ReservationDoesNotExistException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

@Component
@PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
public class LibraryOccupancyQueue extends ArrayBlockingQueue<InmemoryUserDetailDto> {
    private final ArrayList<Long> availableSeatNumberList;
    @Value("{library.properties.enableSeatRandomization}")
    private Boolean randomizeSeatNumberAllocation;
    @Value("{library.properties.numberOfSeats}")
    private Integer numberAvailableOfSeats;
    private int loggedInLibrariansCount;

    public LibraryOccupancyQueue(Long numberOfSeats) {
        super(numberOfSeats.intValue());
        availableSeatNumberList = new ArrayList<>(numberAvailableOfSeats);
        for (int i = 1; i < numberAvailableOfSeats; i++) { //this is to simply store the available seats...
            availableSeatNumberList.add((long) i);
        }
    }

    public synchronized boolean isEmpty() {
        return size() == 0;
    }

    public void updateLoggedInLibrarianCount() {
        ++loggedInLibrariansCount; //TODO still needs work...
    }

    public synchronized Long isLibraryFull() { //check if library is currently filled up at that moment
        if (remainingCapacity() <= 0) throw LibraryClosedException.LibraryMaximumLimitReached();
        if (randomizeSeatNumberAllocation)
            return randomSeatNumber(availableSeatNumberList);
        return availableSeatNumberList.get(0);
    }

    private Long randomSeatNumber(List<Long> availableSeatsList) {
        int randomIndex = new Random().nextInt(availableSeatsList.size());
        return availableSeatsList.get(randomIndex);
    }

    public synchronized boolean signOutUser(InmemoryUserDetailDto inmemoryUserDetailDto) {
        boolean seatFreedUp = freeUpSeat(inmemoryUserDetailDto.getSeatNumber());
        boolean removed = this.removeIf(inMemoryUserDetail -> inMemoryUserDetail.equals(inmemoryUserDetailDto));
        return removed && seatFreedUp;
    }

    private synchronized boolean signInUser(InmemoryUserDetailDto userDetailDto) {
        return add(userDetailDto);
    }

    public synchronized boolean signInStudent(InmemoryUserDetailDto userDetailDto) {
        return signInUser(userDetailDto);
    }

    public synchronized boolean signInLibrarian(InmemoryUserDetailDto userDetailDto) {
        updateLoggedInLibrarianCount();
        return signInUser(userDetailDto);
    }

    public synchronized Reservation hasASession(String placeHolder) {
        List<Reservation> reservationList = stream().filter(userDetail -> userDetail.getIdentifier().equals(placeHolder)).map(InmemoryUserDetailDto::getReservation).toList();
        if (reservationList.size() == 0) //user is not in library...
            throw new ReservationDoesNotExistException();
        return reservationList.get(0);
    }

    public ArrayList<InmemoryUserDetailDto> fetchOccupancyQueueAsList() {
        return (ArrayList<InmemoryUserDetailDto>) this.stream().toList();
    }

    private boolean freeUpSeat(Long seatNumber) { //add seat back to list...
        if (!availableSeatNumberList.contains(seatNumber))
            return availableSeatNumberList.add((long) seatNumber);
        return false;
    }
}