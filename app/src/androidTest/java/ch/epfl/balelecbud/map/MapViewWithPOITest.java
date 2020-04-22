package ch.epfl.balelecbud.map;

import android.app.PendingIntent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.location.LocationRequest;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.friendship.FriendshipUtils;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestType;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static ch.epfl.balelecbud.testUtils.TestAsyncUtils.runOnUIThreadAndWait;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.alex;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.celine;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.karim;

@RunWith(AndroidJUnit4.class)
public class MapViewWithPOITest {
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();

    private final PointOfInterest atm = new PointOfInterest(new Location(1, 2), "credit suisse", PointOfInterestType.ATM);
    private final PointOfInterest bar = new PointOfInterest(new Location(4, 5), "bar ic", PointOfInterestType.BAR);
    private final PointOfInterest food = new PointOfInterest(new Location(1, 3), "kebab", PointOfInterestType.FOOD);
    private final PointOfInterest help = new PointOfInterest(new Location(1, 2), "first aid", PointOfInterestType.FIRST_AID);
    private final PointOfInterest stage = new PointOfInterest(new Location(13, 29), "grande scene", PointOfInterestType.STAGE);
    private final PointOfInterest wc = new PointOfInterest(new Location(13, 2), "WC", PointOfInterestType.WC);

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
                    mockDB.storeDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, atm);
//                    mockDB.storeDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, bar);
//                    mockDB.storeDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, food);
//                    mockDB.storeDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, wc);
//                    mockDB.storeDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, help);
//                    mockDB.storeDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH, stage);
                }
            };

    @After
    public void cleanup() {
        mockDB.resetDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH);
    }

    private void assertNameAndLocation(MyMarker.Builder markerBuilder, TestAsyncUtils sync, PointOfInterest poi) {
        sync.assertEquals(poi.getName(), markerBuilder.getTitle());
        sync.assertEquals(poi.getLocation(), markerBuilder.getLocation());
    }

    @Test
    public void onePoiShowsOneMarkerOnMap() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(new MyMap() {
            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.assertNotNull(markerBuilder);
                assertNameAndLocation(markerBuilder, sync, atm);
                sync.call();
                return null;
            }

            @Override
            public void initialiseMap(boolean locationEnabled) {
                sync.call();
            }
        }));
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }


}
