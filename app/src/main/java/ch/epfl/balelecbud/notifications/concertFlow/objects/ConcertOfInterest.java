package ch.epfl.balelecbud.notifications.concertFlow.objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import ch.epfl.balelecbud.schedule.models.Slot;

@Entity
public class ConcertOfInterest {
    @PrimaryKey
    public Slot slot;
}
