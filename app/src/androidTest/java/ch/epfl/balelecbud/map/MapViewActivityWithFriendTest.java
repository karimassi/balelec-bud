package ch.epfl.balelecbud.map;

import android.app.PendingIntent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.location.LocationRequest;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.BasicActivityTest;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.friendship.FriendshipUtils;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static ch.epfl.balelecbud.testUtils.TestAsyncUtils.runOnUIThreadAndWait;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.alex;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.celine;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.karim;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MapViewActivityWithFriendTest extends BasicActivityTest {
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();
    private final Location karimLocation = new Location(2, 4);
    @Rule
    public final ActivityTestRule<MapViewActivity> mActivityRule =
            new ActivityTestRule<MapViewActivity>(MapViewActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    BalelecbudApplication.setAppDatabaseWrapper(mockDB);
                    BalelecbudApplication.setAppAuthenticator(mockAuth);
                    MapViewActivity.setMockCallback(mapboxMap -> {
                    });
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
                    FriendshipUtils.acceptRequest(alex);
                    mockDB.storeDocumentWithID(DatabaseWrapper.LOCATIONS_PATH, karim.getUid(), karimLocation);
                }
            };
    private final Location newKarimLocation = new Location(1, 2);
    private final Location alexLocation = new Location(3, 3);

    @After
    public void cleanup() {
        mockDB.resetDocument(DatabaseWrapper.LOCATIONS_PATH);
        mockDB.resetFriendshipsAndRequests();
    }

    @Test
    public void whenHaveOneFriendOneMarkerOnTheMap() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(new MyMap() {
            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.assertNotNull(markerBuilder);
                assertNameAndLocation(markerBuilder, sync, karim, karimLocation);
                sync.call();
                return null;
            }

            @Override
            public void initialiseMap(boolean locationEnabled, Location defaultLocation) {
                sync.call();
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
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.assertNotNull(markerBuilder);
                assertNameAndLocation(markerBuilder, sync, karim, karimLocation);
                sync.call();
                return location -> {
                    sync.call();
                    sync.assertEquals(newKarimLocation, location);
                };
            }

            @Override
            public void initialiseMap(boolean locationEnabled, Location defaultLocation) {
                sync.call();
            }
        }));
        sync.waitCall(2);
        mockDB.storeDocumentWithID(DatabaseWrapper.LOCATIONS_PATH, karim.getUid(), newKarimLocation);
        sync.waitCall(3);
        sync.assertCalled(3);
        sync.assertNoFailedTests();
    }

    @Test
    public void whenAFriendHaveNoLocationNothingIsShownOnTheMap() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(assertKarimThenAlexLocation(sync)));
        sync.waitCall(2);
        mockDB.storeDocumentWithID(DatabaseWrapper.LOCATIONS_PATH, alex.getUid(), alexLocation);
        sync.waitCall(3);
        sync.assertCalled(3);
        sync.assertNoFailedTests();
    }

    @NotNull
    private MyMap assertKarimThenAlexLocation(TestAsyncUtils sync) {
        return new MyMap() {
            private int times = 0;

            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.assertNotNull(markerBuilder);
                if (times == 0) {
                    assertNameAndLocation(markerBuilder, sync, karim, karimLocation);
                } else {
                    assertNameAndLocation(markerBuilder, sync, alex, alexLocation);
                }
                sync.call();
                times += 1;
                return null;
            }

            @Override
            public void initialiseMap(boolean locationEnabled, Location defaultLocation) {
                sync.call();
            }
        };
    }

    private void assertNameAndLocation(MyMarker.Builder markerBuilder, TestAsyncUtils sync, User user, Location userLocation) {
        sync.assertEquals(user.getDisplayName(), markerBuilder.getTitle());
        sync.assertEquals(userLocation, markerBuilder.getLocation());
        sync.assertEquals(MarkerType.FRIEND, markerBuilder.getType());
    }

    @Test
    public void signOutClearListeners() {
        assertEquals(2, mockDB.getFriendsLocationListenerCount());
        //super.signOutFromDrawer();
        assertEquals(0, mockDB.getFriendsLocationListenerCount());
    }

    @Override
    protected void setIds() {
        setIds(R.id.map_activity_drawer_layout, R.id.map_activity_nav_view);
    }
}
