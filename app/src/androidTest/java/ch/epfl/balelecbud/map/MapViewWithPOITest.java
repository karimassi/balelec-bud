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
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterest;
import ch.epfl.balelecbud.pointOfInterest.PointOfInterestType;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static ch.epfl.balelecbud.testUtils.TestAsyncUtils.runOnUIThreadAndWait;
import static ch.epfl.balelecbud.util.database.MockDatabaseWrapper.celine;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class MapViewWithPOITest {
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();

    private final PointOfInterest atm = new PointOfInterest(new Location(1, 2),
            "credit suisse", PointOfInterestType.ATM);

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
                }

                @Override
                protected void afterActivityFinished() {
                    super.afterActivityFinished();
                    mockDB.resetMockDatabase();
                }
            };

    @After
    public void cleanup() {
        mockDB.resetDocument(DatabaseWrapper.POINT_OF_INTEREST_PATH);
    }

    private void assertNameAndLocation(MyMarker.Builder markerBuilder, TestAsyncUtils sync, PointOfInterest poi) {
        sync.assertEquals(poi.getName(), markerBuilder.getTitle());
        sync.assertEquals(poi.getLocation(), markerBuilder.getLocation());
        sync.assertEquals(MarkerType.getMarkerType(poi.getType()), MarkerType.ATM);
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
            public void initialiseMap(boolean locationEnabled, Location defaultLocation) {
                sync.assertThat(defaultLocation, is(Location.DEFAULT_LOCATION));
                sync.call();
            }
        }));
        sync.waitCall(2);
        sync.assertCalled(2);
        sync.assertNoFailedTests();
    }
}
