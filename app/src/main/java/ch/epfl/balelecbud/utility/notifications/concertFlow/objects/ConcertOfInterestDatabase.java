package ch.epfl.balelecbud.utility.notifications.concertFlow.objects;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ch.epfl.balelecbud.model.Slot;

@Database(entities = {Slot.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ConcertOfInterestDatabase extends RoomDatabase {
    public abstract ConcertOfInterestDAO getConcertOfInterestDAO();
}
