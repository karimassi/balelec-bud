package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.List;

import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {
    private final float DEFAULT_ZOOM = 17;

    private Location location;
    private GoogleMap googleMap;
    private Task<android.location.Location> locationResult;
    private static boolean locationEnabled;

    private final OnCompleteListener<android.location.Location> callback =
            new OnCompleteListener<android.location.Location>() {
                @Override
                public void onComplete(@NonNull Task<android.location.Location> task) {
                    setLocationFrom(task.getResult(), task.isSuccessful());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLatLng(location), DEFAULT_ZOOM));
                }
            };

    public static final String LOCATION_KEY = "location";
    public static final String FRIENDS_KEY = "friends";

    private HashMap<String, LatLng> friendsLocation;

    private CollectionReference userCollectionRef = FirebaseFirestore.getInstance().collection("users");

    // TO CHANGE : get token of current user
    private DocumentReference localUserDocRef = userCollectionRef.document("32fHlBbtC0fCPVa9TUGrc0Z776E3");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setDefaultLocation();
        setLocationPermission();
        setLocationResult();

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
        displayFriendsLocation(map);
        googleMap = map;

        googleMap.getUiSettings().setCompassEnabled(true);

        googleMap.setMyLocationEnabled(locationEnabled);
        googleMap.getUiSettings().setMyLocationButtonEnabled(locationEnabled);

        if(locationEnabled)
            locationResult.addOnCompleteListener(this, callback);
        else {
            googleMap.addMarker(new MarkerOptions().position(getLatLng(location)).title("Default Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLatLng(location), DEFAULT_ZOOM));
        }
    }

    protected void setLocationResult() {
        if(locationEnabled) {
            locationResult = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
        } else {
            locationResult = null;
        }
    }

    protected void setLocationPermission() {
        locationEnabled = LocationUtil.isLocationActive();
    }

    protected void setLocationFrom(android.location.Location deviceLocation, boolean locationEnabled) {
        if(locationEnabled && deviceLocation != null) {
            location = new Location(deviceLocation);
        }
    }

    protected void setLocationFrom(LatLng latLng) {
        if(latLng != null) {
            location = new Location(latLng);
        }
    }

    protected void setLocation(Location location) {
        if (location != null) {
            this.location = location;
        }
    }

    private void setDefaultLocation() {
        final double defaultLat = Double.parseDouble(getString(R.string.default_lat));
        final double defaultLng = Double.parseDouble(getString(R.string.default_lng));
        final Location defaultLocation = new Location(defaultLat, defaultLng);
        setLocation(defaultLocation);
    }

    public Location getLocation() {
        return location;
    }

    public static boolean getLocationPermission() {
        return locationEnabled;
    }

    public Task<android.location.Location> getLocationResult() {
        return locationResult;
    }

    public LatLng getLatLng(Location location) {
        if(location != null) {
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        else return null;
    }

    public void displayFriendsLocation(GoogleMap map){
        for (String name : friendsLocation.keySet()) {
            map.addMarker(new MarkerOptions().position(friendsLocation.get(name)).title(name));
        }
    }

    public void updateFriendLocation(){
        localUserDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
    
    public GoogleMap getGoogleMap() {
        return googleMap;
    }
}