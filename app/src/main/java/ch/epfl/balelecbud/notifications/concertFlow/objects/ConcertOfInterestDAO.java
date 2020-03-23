package ch.epfl.balelecbud.notifications.concertFlow.objects;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ch.epfl.balelecbud.schedule.models.Slot;

@Dao
public interface ConcertOfInterestDAO {
    @Query("SELECT * FROM Slot")
    List<Slot> getAllConcertOfInterest();

    @Insert
    void insertConcert(Slot concert);

    @Delete
    void removeConcert(Slot concert);

    @Query("DELETE FROM Slot WHERE Slot.id = :id")
    void removeConcertById(int id);
}
