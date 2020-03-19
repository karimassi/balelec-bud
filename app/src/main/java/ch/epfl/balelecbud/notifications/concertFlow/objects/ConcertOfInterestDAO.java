package ch.epfl.balelecbud.notifications.concertFlow.objects;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import ch.epfl.balelecbud.schedule.models.Slot;

@Dao
public interface ConcertOfInterestDAO {
    @Query("SELECT * FROM ConcertOfInterest")
    List<Slot> getAllConcertOfInterest();


}
