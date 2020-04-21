package ch.epfl.balelecbud.map;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BasicActivity;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.friendship.FriendshipUtils;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppAuthenticator;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabaseWrapper;

public class MapViewActivity extends BasicActivity {

    private final static String TAG = MapViewActivity.class.getSimpleName();
    private static OnMapReadyCallback mockCallback;
    private MapView mapView;
    private MyMap myMap;
    private Map<User, MyMarker> friendsMarkers = new HashMap<>();
    private Map<User, Location> waitingFriendsLocation = new HashMap<>();

    private Map<MarkerType, Icon> icons;

    @VisibleForTesting
    public static void setMockCallback(OnMapReadyCallback mockCallback) {
        MapViewActivity.mockCallback = mockCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        if (mockCallback != null) {
            mapView.getMapAsync(mockCallback);
        } else {
            mapView.getMapAsync(
                    mapboxMap -> onMapReady(new MapboxMapAdapter(mapboxMap)));
        }

        setupMapIcons();

        configureToolBar(R.id.map_activity_toolbar);
        configureDrawerLayout(R.id.map_activity_drawer_layout);
        configureNavigationView(R.id.map_activity_nav_view);
        requestFriendsLocations();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (getAppAuthenticator().getCurrentUser() != null) {
            unregisterListeners();
        }
    }

    @Override
    protected void signOut() {
        unregisterListeners();
        super.signOut();
    }

    public void onMapReady(MyMap map) {
        myMap = map;
        myMap.initialiseMap(LocationUtil.isLocationActive());
        displayWaitingFriends(myMap);
    }


    private void unregisterListeners() {
        FriendshipUtils.getFriendsUids(getAppAuthenticator().getCurrentUser())
                .thenAccept(friendsIds -> friendsIds.forEach(id -> getAppDatabaseWrapper()
                        .unregisterDocumentListener(DatabaseWrapper.LOCATIONS_PATH, id)));
    }

    public void displayWaitingFriends(MyMap map) {
        for (User friend : waitingFriendsLocation.keySet()) {
            friendsMarkers.put(friend, map.addMarker(new MyMarker.Builder()
                    .location(waitingFriendsLocation.get(friend))
                    .title(friend.getDisplayName())
                    .icon(icons.get(MarkerType.FRIEND))));
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
                    .title(friend.getDisplayName())
                    .icon(icons.get(MarkerType.FRIEND))));
        }
    }

    private void setupMapIcons() {
        icons = new HashMap<>();
        IconFactory iconFactory = IconFactory.getInstance(getApplicationContext());
        Drawable iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.map);
        Bitmap bitmap = ((BitmapDrawable) iconDrawable).getBitmap();
        Icon icon = iconFactory.fromBitmap(bitmap);
        icons.put(MarkerType.FRIEND, icon);
    }
}