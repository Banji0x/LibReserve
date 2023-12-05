package dev.banji.LibReserve.model;

import dev.banji.LibReserve.config.properties.LibraryConfigurationProperties;
import dev.banji.LibReserve.exceptions.LibraryClosedException;
import dev.banji.LibReserve.model.dtos.CurrentStudentDetailDto;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

@Component
//@PreAuthorize("hasAuthority('SCOPE_LIBRARIAN')")
public class LibraryOccupancyQueue extends ArrayBlockingQueue<InmemoryUserDetailDto> {
    @Getter
    private final ArrayList<Long> internalAvailableSeatNumberList;
    private final LibraryConfigurationProperties libraryConfigurationProperties;


    public LibraryOccupancyQueue(LibraryConfigurationProperties libraryConfigurationProperties) {
        super(libraryConfigurationProperties.getNumberOfSeats().intValue());
        this.libraryConfigurationProperties = libraryConfigurationProperties;
        internalAvailableSeatNumberList = new ArrayList<>();
        for (int i = 1; i < libraryConfigurationProperties.getNumberOfSeats().intValue(); i++) { //this is to simply store the available seats...
            internalAvailableSeatNumberList.add((long) i);
        }
    }

    public synchronized boolean isSeatTaken(Long seatNumber) {
        return !internalAvailableSeatNumberList.contains(seatNumber);
    }

    public synchronized boolean isEmpty() {
        return size() == 0;
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

    public synchronized boolean signOutLibrarian(CurrentLibrarianDetailDto librarianDetailDto) {
        return signOutUser(librarianDetailDto);
    }

    private synchronized boolean signInUser(InmemoryUserDetailDto userDetailDto) {
        return add(userDetailDto);
    }

    public synchronized boolean signInStudent(InmemoryUserDetailDto userDetailDto) {
        return signInUser(userDetailDto);
    }

    public synchronized void signInLibrarian(InmemoryUserDetailDto userDetailDto) {
        signInUser(userDetailDto);
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
        if (!internalAvailableSeatNumberList.contains(seatNumber))
            return internalAvailableSeatNumberList.add(seatNumber);
        return false;
    }

    public Optional<Long> seatNumberResolver(List<Long> seatList, boolean isLibrarian) {
        boolean randomizeSeatsAllocation = libraryConfigurationProperties.getEnableSeatRandomization();
        List<Long> availableSeatList = seatList.stream().filter(seat -> !isSeatTaken(seat)).toList();

        if (isLibrarian) {
            if (seatList.isEmpty()) { //base condition
                return Optional.empty();
            }
            if (availableSeatList.isEmpty()) { //recursive condition
                //then check the general list...
                seatNumberResolver(internalAvailableSeatNumberList, true);
            }
        }

        if (randomizeSeatsAllocation) {
            int randomIndex = new Random().nextInt(availableSeatList.size());
            Long seatNumber = availableSeatList.get(randomIndex);
            //update internal available seat list
            removeSeatFromList(seatNumber);
            return Optional.of(seatNumber);
        }
        Long seatNumber = availableSeatList.get(0);
        //update internal available seat list
        removeSeatFromList(seatNumber);
        return Optional.of(seatNumber);

    }


    private void removeSeatFromList(Long seatNumber) {
        internalAvailableSeatNumberList.removeIf(seat -> seat.equals(seatNumber));
    }
}