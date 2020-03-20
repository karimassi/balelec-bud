package ch.epfl.balelecbud.notifications.concertFlow.objects;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ch.epfl.balelecbud.schedule.models.Slot;

@Database(entities = {Slot.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ConcertOfInterestDatabase extends RoomDatabase {
    public abstract ConcertOfInterestDAO getConcertOfInterestDAO();
}
