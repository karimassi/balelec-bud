package ch.epfl.balelecbud.Location;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.GeoPoint;

public interface LocationFirestore {
    void handleGeoPoint(GeoPoint gp, OnCompleteListener<Void> callback);
}
