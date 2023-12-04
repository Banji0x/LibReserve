package dev.banji.LibReserve.model;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.exceptions.LibraryClosedException;
import dev.banji.LibReserve.model.dtos.CurrentStudentDetailDto;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

@Component
//@PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
public class LibraryOccupancyQueue extends ArrayBlockingQueue<InmemoryUserDetailDto> {
    @Getter
    private final ArrayList<Long> availableSeatNumberList;


    public LibraryOccupancyQueue(LibraryConfigurationProperties libraryConfigurationProperties) {
        super(libraryConfigurationProperties.getNumberOfSeats().intValue());
        availableSeatNumberList = new ArrayList<>();
        for (int i = 1; i < libraryConfigurationProperties.getNumberOfSeats().intValue(); i++) { //this is to simply store the available seats...
            availableSeatNumberList.add((long) i);
        }
    }

    public synchronized boolean isEmpty() {
        return size() == 0;
    }

    public void updateLoggedInLibrarianCount() {
//        ++loggedInLibrariansCount; //TODO still needs work...
    }

    public synchronized void isLibraryFull() { //check if library is currently filled up at that moment
        if (remainingCapacity() <= 0) throw LibraryClosedException.LibraryMaximumLimitReached();
    }

    private synchronized boolean signOutUser(InmemoryUserDetailDto inmemoryUserDetailDto) {
        boolean seatFreedUp = freeUpSeat(inmemoryUserDetailDto.getSeatNumber());
        boolean removed = this.removeIf(inMemoryUserDetail -> inMemoryUserDetail.equals(inmemoryUserDetailDto));
        return removed && seatFreedUp;
    }

    /**
     * This is just a helper method...
     *
     * @return true if operation is successful. else false.
     */
    public synchronized boolean signOutStudent(CurrentStudentDetailDto studentDetailDto) {
        return signOutUser(studentDetailDto);
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

    public synchronized Optional<Reservation> isUserPresentInLibrary(String placeHolder) {
        return stream()
                .filter(userDetail -> userDetail.getIdentifier().equals(placeHolder))
                .map(InmemoryUserDetailDto::getReservation).findFirst();
    }

    /**
     * This method *only* accepts a reservationCode
     *
     * @param reservationCode - the reservation code
     * @return Optional<StudentReservation>
     */
    public synchronized Optional<StudentReservation> isStudentPresentInLibrary(String reservationCode) {
        return stream()
                .filter(userDetail -> ((CurrentStudentDetailDto) userDetail).getReservationCode().equals(reservationCode))
                .map(inmemoryUserDetailDto -> (StudentReservation) inmemoryUserDetailDto.getReservation()).findFirst();
    }

    public ArrayList<InmemoryUserDetailDto> fetchOccupancyQueueAsList() {
        return (ArrayList<InmemoryUserDetailDto>) this.stream().toList();
    }

    private boolean freeUpSeat(Long seatNumber) { //add seat back to list...
        if (!availableSeatNumberList.contains(seatNumber))
            return availableSeatNumberList.add(seatNumber);
        return false;
    }

}