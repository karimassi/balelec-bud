package ch.epfl.balelecbud.Location;

import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class FireBaseLocationAdapter implements LocationFirestore {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final static String GEOPOINT_KEY = "ch.epfl.balelecbud.Location.GEOPOINT_KEY";
    private final static String TAG = FireBaseLocationAdapter.class.getSimpleName();

    @Override
    public void handleGeoPoint(GeoPoint gp, OnCompleteListener<Void> callback) {
        Log.d(TAG, "handleGeoPoint: ANDROID_ID " + Settings.Secure.ANDROID_ID);
        db.document("locations/location")
                .update(Settings.Secure.ANDROID_ID, gp).addOnCompleteListener(callback);
    }
}
