package ch.epfl.balelecbud.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.MapView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.friendship.FriendshipUtils;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;
import static ch.epfl.balelecbud.location.LocationUtil.isLocationActive;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    private final static String TAG = MapViewFragment.class.getSimpleName();
    private static com.google.android.gms.maps.OnMapReadyCallback mockCallback;
    private MapView mapView;
    private MyMap myMap;
    private Map<User, MyMarker> friendsMarkers = new HashMap<>();
    private Map<User, Location> waitingFriendsLocation = new HashMap<>();
    private Bundle savedInstance;

    @VisibleForTesting
    public static void setMockCallback(com.google.android.gms.maps.OnMapReadyCallback mockCallback) {
        MapViewFragment.mockCallback = mockCallback;
    }

    public static MapViewFragment newInstance() {
        return (new MapViewFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstance = savedInstanceState;
        return inflater.inflate(R.layout.activity_map, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView = getView().findViewById(R.id.mapView);
        mapView.onCreate(savedInstance);
        if (mockCallback != null)
            mapView.getMapAsync(mockCallback);
        else
            mapView.getMapAsync(googleMap -> this.onMapReady(new GoogleMapAdapter(googleMap)));

        requestFriendsLocations();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(MyMap map) {
        myMap = map;
        myMap.setMyLocationEnabled(isLocationActive());
        displayWaitingFriends(myMap);
    }

    public void displayWaitingFriends(MyMap map) {
        for (User friend : waitingFriendsLocation.keySet()) {
            friendsMarkers.put(friend, map.addMarker(new MyMarker.Builder()
                    .location(waitingFriendsLocation.get(friend))
                    .title(friend.getDisplayName())));
        }
        waitingFriendsLocation.clear();
    }

    private void requestFriendsLocations() {
        Log.d(TAG, "requestFriendsLocations: requesting friendsUids");
        CompletableFuture<List<String>> friendsUids = FriendshipUtils.getFriendsUids(getAppAuthenticator().getCurrentUser());
        friendsUids.thenAccept(friendsIds -> Log.d(TAG, "requestFriendsLocations: friendsIds = [" + friendsIds.toString() + "]"));
        friendsUids.thenAccept(this::listenFriendsLocationById);
    }

    private void listenFriendsLocationById(List<String> friendIds) {
        for (String friendId : friendIds) {
            CompletableFuture<User> friend = FriendshipUtils.getUserFromUid(friendId);
            friend.thenAccept(user -> Log.d(TAG, "listenFriendsLocationById: get user = [" + user.toString() + "]"));
            friend.thenAccept(this::listenFriendLocation);
        }
    }

    private void listenFriendLocation(User friend) {
        Log.d(TAG, "listenFriendLocation() called with: friend = [" + friend + "]");
        getAppDatabaseWrapper().listenDocument(DatabaseWrapper.LOCATIONS_PATH, friend.getUid(),
                location -> updateFriendLocation(friend, location), Location.class);
    }

    private void updateFriendLocation(User friend, Location location) {
        Log.d(TAG, "updateFriendLocation() called with: friend = [" + friend + "], location = [" + location + "]");
        if (myMap == null) {
            waitingFriendsLocation.put(friend, location);
        } else if (friendsMarkers.containsKey(friend)) {
            friendsMarkers.get(friend).setLocation(location);
        } else {
            friendsMarkers.put(friend, myMap.addMarker(new MyMarker.Builder()
                    .location(location)
                    .title(friend.getDisplayName())));
        }
    }
}