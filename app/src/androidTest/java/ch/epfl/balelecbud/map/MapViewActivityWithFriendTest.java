package ch.epfl.balelecbud.map;

import android.app.PendingIntent;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.location.LocationRequest;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.BasicActivityTest;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.friendship.FriendshipUtils;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static ch.epfl.balelecbud.testUtils.TestAsyncUtils.runOnUIThreadAndWait;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.celine;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.karim;
import static org.junit.Assert.assertEquals;

public class MapViewActivityWithFriendTest extends BasicActivityTest {
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final Location karimLocation = new Location(2, 4);
    private final Location newKarimLocation = new Location(1, 2);

    @Rule
    public final ActivityTestRule<MapViewActivity> mActivityRule =
            new ActivityTestRule<MapViewActivity>(MapViewActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    BalelecbudApplication.setAppDatabaseWrapper(mockDB);
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    MapViewActivity.setMockCallback(googleMap -> {});
                    LocationUtil.setLocationClient(new LocationClient() {
                        @Override
                        public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {

                        }

                        @Override
                        public void removeLocationUpdates(PendingIntent intent) {

                        }
                    });
                    mockAuth.setCurrentUser(celine);
                    FriendshipUtils.acceptRequest(karim);
                    mockDB.storeDocumentWithID(DatabaseWrapper.LOCATIONS_PATH, karim.getUid(), karimLocation);
                }
            };

    @Test
    public void whenHaveOneFriendOneMarkerOnTheMap() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(new MyMap() {
            @Override
            public void setMyLocationEnabled(boolean locationEnabled) {
                sync.call();
            }

            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.assertNotNull(markerBuilder);
                sync.assertEquals(karim.getDisplayName(), markerBuilder.getTitle());
                sync.assertEquals(karimLocation, markerBuilder.getLocation());
                sync.call();
                return null;
            }
        }));
        sync.waitCall(2);
        sync.assertCalled(2);
        sync.assertNoFailedTests();
    }

    @Test
    public void whenFriendLocationUpdateMarkerMoved() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(new MyMap() {
            @Override
            public void setMyLocationEnabled(boolean locationEnabled) {
                sync.call();
            }

            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.assertNotNull(markerBuilder);
                sync.assertEquals(karim.getDisplayName(), markerBuilder.getTitle());
                sync.assertEquals(karimLocation, markerBuilder.getLocation());
                sync.call();
                return location -> {
                    sync.call();
                    sync.assertEquals(newKarimLocation, location);
                };
            }
        }));
        sync.waitCall(2);
        mockDB.storeDocumentWithID(DatabaseWrapper.LOCATIONS_PATH, karim.getUid(), newKarimLocation);
        sync.waitCall(3);
        sync.assertCalled(3);
        sync.assertNoFailedTests();
    }

    @Test
    public void signOutClearListeners() {
        assertEquals(1, mockDB.getFriendsLocationListenerCount());
        super.signOutFromDrawer();
        assertEquals(0, mockDB.getFriendsLocationListenerCount());
    }

    @Override
    protected void setIds() {
        setIds(R.id.map_activity_drawer_layout, R.id.map_activity_nav_view);
    }
}
