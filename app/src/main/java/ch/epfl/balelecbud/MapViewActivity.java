package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import ch.epfl.balelecbud.user.models.User;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private double defaultLat;
    private double defaultLng;

    private LatLng position;

    public static final String LOCATION_KEY = "location";
    public static final String FRIENDS_KEY = "friends";

    private  HashMap<String, LatLng> friendsLocation;

    private CollectionReference userCollectionRef = FirebaseFirestore.getInstance().collection("users");

    // TO CHANGE : get token of current user
    private DocumentReference localUserDocRef = userCollectionRef.document("32fHlBbtC0fCPVa9TUGrc0Z776E3");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        defaultLat = Double.parseDouble(getString(R.string.default_lat));
        defaultLng = Double.parseDouble(getString(R.string.default_lng));
        LatLng default_location = new LatLng(defaultLat, defaultLng);

        setPosition(default_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        friendsLocation = new HashMap<String,LatLng>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateFriendLocation();

    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(position).title("defaultPosition"));
        map.moveCamera(CameraUpdateFactory.newLatLng(position));
        displayFriendsLocation(map);
    }

    public void setPosition(LatLng position) {
        if (position != null) {
            this.position = position;
        }
    }


    public void displayFriendsLocation(GoogleMap map){
        for (String name : friendsLocation.keySet()) {
            map.addMarker(new MarkerOptions().position(friendsLocation.get(name)).title(name));
        }
    }

    public void updateFriendLocation(){
        localUserDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                       @Override
                                                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                           if (documentSnapshot.exists()) {
                                                               List<String> friends = (List<String>) documentSnapshot.get("firends");
                                                               for (String friend : friends) {
                                                                   DocumentReference friendRef = userCollectionRef.document(friend);
                                                                   friendRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                       @Override
                                                                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                           if (documentSnapshot.exists()) {
                                                                               LatLng friendLocation = getPosition((GeoPoint) documentSnapshot.get("location"));
                                                                               String friendName = (String) documentSnapshot.get("name");
                                                                               friendsLocation.put(friendName, friendLocation);
                                                                           }
                                                                       }
                                                                   });
                                                               }
                                                           }
                                                       }
                                                   });

    }


    public LatLng getPosition(GeoPoint point){
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

    public LatLng getPosition() {
        return this.position;
    }
}