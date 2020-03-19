package ch.epfl.balelecbud.notifications.concertFlow.objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

import ch.epfl.balelecbud.schedule.models.Slot;

@Entity
public class ConcertOfInterest {
    @PrimaryKey
    public int id;

    public final Slot slot;

    public ConcertOfInterest(Slot slot) {
        this.slot = slot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcertOfInterest that = (ConcertOfInterest) o;
        return slot.equals(that.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot);
    }
}
