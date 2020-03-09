package ch.epfl.balelecbud.Location;

import android.location.Location;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class FireBaseLocationAdapter implements LocationFirestore {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final static String TAG = FireBaseLocationAdapter.class.getSimpleName();

    private final static String GEOPOINT_KEY = "ch.epfl.balelecbud.Location.GEOPOINT_KEY";

    @Override
    public void handleLocation(Location l) {
        if (l == null) return;
        double latitude = l.getLatitude();
        double longitude = l.getLongitude();
        GeoPoint point = new GeoPoint(latitude, longitude);
        db.collection("location").document(Settings.Secure.ANDROID_ID).
                update(GEOPOINT_KEY, point).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: location successfully updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: failed to updated location", e);
            }
        });
    }
}
