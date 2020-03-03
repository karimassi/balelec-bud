package ch.epfl.balelecbud.schedule;


import androidx.annotation.Nullable;

import java.util.Objects;

import ch.epfl.balelecbud.utils.Preconditions;

public class Concert {

    private final TimeSlot timeslot;
    private final Band band;

    public Concert(TimeSlot timeslot, Band band) {
        Preconditions.checkNonNull(timeslot);
        Preconditions.checkNonNull(band);
        this.timeslot = timeslot;
        this.band = band;
    }

    public TimeSlot getTimeSlot() {
        return timeslot;
    }

    public Band getBand() {
        return band;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Concert)) {
            return false;
        }
        Concert other = (Concert) obj;
        return timeslot.equals(other.timeslot) && band.equals(other.band);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeslot, band);
    }

    static class Id {

        private final int id;

        public Id(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }
}
