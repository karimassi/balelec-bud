package ch.epfl.balelecbud.FestivalInformation;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public interface FestivalInformationDatabase {

    void addListener(FestivalInformationListener listener);
    void unregisterListener();
}
