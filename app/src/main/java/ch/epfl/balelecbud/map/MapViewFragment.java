package ch.epfl.balelecbud.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.friendship.FriendshipUtils;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class MapViewFragment extends Fragment {
    public final static String TAG = MapViewFragment.class.getSimpleName();
    private static com.mapbox.mapboxsdk.maps.OnMapReadyCallback mockCallback;
    private MapView mapView;
    private MyMap myMap;
    private Map<User, MyMarker> friendsMarkers = new HashMap<>();
    private List<PointOfInterest> waitingPOI = new LinkedList<>();
    private Map<User, Location> waitingFriendsLocation = new HashMap<>();
    private Location defaultLocation;

    @VisibleForTesting
    public static void setMockCallback(com.mapbox.mapboxsdk.maps.OnMapReadyCallback mockCallback) {
        MapViewFragment.mockCallback = mockCallback;
    }

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        View inflatedView = inflater.inflate(R.layout.fragment_main_map, container, false);
        mapView = inflatedView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        if (mockCallback != null) {
            mapView.getMapAsync(mockCallback);
        } else {
            mapView.getMapAsync(mapboxMap -> onMapReady(new MapboxMapAdapter(mapboxMap)));
        }

        Bundle arguments = getArguments();
        Location location = arguments != null ? arguments.getParcelable("location") : null;
        defaultLocation = location == null ? Location.DEFAULT_LOCATION : location;
        requestFriendsLocations();
        displayPointsOfInterests();
        return inflatedView;
    }

    @Override
    public void onStart() {
        super.onStart();
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
        if (getAppAuthenticator().getCurrentUser() != null) {
            unregisterListeners();
        }
    }

    public void onMapReady(MyMap map) {
        myMap = map;
        myMap.initialiseMap(LocationUtil.isLocationActive(), this.defaultLocation);
        displayWaitingFriends();
        displayWaitingPOI();
    }


    private void unregisterListeners() {
        FriendshipUtils.getFriendsUids(getAppAuthenticator().getCurrentUser())
                .thenAccept(friendsIds -> friendsIds.forEach(id -> getAppDatabaseWrapper()
                        .unregisterDocumentListener(DatabaseWrapper.LOCATIONS_PATH, id)));
    }

    public void displayWaitingPOI() {
        for (PointOfInterest poi : waitingPOI) {
            myMap.addMarker(new MyMarker.Builder()
                    .location(poi.getLocation())
                    .type(MarkerType.getMarkerType(poi.getType()))
                    .title(poi.getName()));
        }
        waitingPOI.clear();
    }

    public void displayWaitingFriends() {
        for (User friend : waitingFriendsLocation.keySet()) {
            friendsMarkers.put(friend, myMap.addMarker(new MyMarker.Builder()
                    .location(waitingFriendsLocation.get(friend))
                    .title(friend.getDisplayName())
                    .type(MarkerType.FRIEND)));
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
        if (location == null)
            return;
        if (myMap == null) {
            waitingFriendsLocation.put(friend, location);
        } else if (friendsMarkers.containsKey(friend)) {
            friendsMarkers.get(friend).setLocation(location);
        } else {
            friendsMarkers.put(friend, myMap.addMarker(new MyMarker.Builder()
                    .location(location)
                    .title(friend.getDisplayName())
                    .type(MarkerType.FRIEND)));
        }
    }

    private void displayPointsOfInterests() {
        getAppDatabaseWrapper().query(new MyQuery(DatabaseWrapper.POINT_OF_INTEREST_PATH, new LinkedList<>()),
                PointOfInterest.class).whenComplete((pointOfInterests, throwable) -> {
            for (PointOfInterest poi : pointOfInterests) {
                if (myMap == null) {
                    waitingPOI.add(poi);
                } else {
                    myMap.addMarker(new MyMarker.Builder()
                            .location(poi.getLocation())
                            .type(MarkerType.getMarkerType(poi.getType()))
                            .title(poi.getName()));
                }
            }
        });
    }
}