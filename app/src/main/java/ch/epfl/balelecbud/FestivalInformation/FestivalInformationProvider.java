package ch.epfl.balelecbud.FestivalInformation;

import com.google.firebase.database.FirebaseDatabase;

public class FestivalInformationProvider {

    public FestivalInformationDatabase subscribe(FestivalInformationListener listener) {
        FestivalInformationDatabase db = new FirebaseFestivalInformationDatabase();
        db.addListener(listener);
        return db;
    }

}
