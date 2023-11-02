package dev.banji.LibReserve.exceptions;

import java.time.LocalTime;

public class EarlyCheckInException extends LibraryRuntimeException {

    public EarlyCheckInException(LocalTime timeReservationWasMadeFor) {
        super("Early check-in are not allowed. Kindly wait till " + timeReservationWasMadeFor);
    }
}
