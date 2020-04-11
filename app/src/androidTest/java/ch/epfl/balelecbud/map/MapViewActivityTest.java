package ch.epfl.balelecbud.map;

import android.app.PendingIntent;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.BasicActivityTest;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.testUtils.TestAsyncUtils.runOnUIThreadAndWait;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class MapViewActivityTest extends BasicActivityTest {
    private final MockDatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();

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
                    mockAuth.setCurrentUser(MockDatabaseWrapper.celine);
                }
            };

    private final Location celineLocation = new Location(1, 2);
    private final Location karimLocation = new Location(2, 4);
    private final double testLatitude = -12.12;
    private final double testLongitude = -77.03;
    private final Location testLocation = new Location(testLatitude, testLongitude);
    private final LatLng testLatLng = testLocation.toLatLng();
    private Location oldMapLocation;
    private Location newMapLocation;

    @Test
    public void testMapViewIsNotNull() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        View viewById = mActivity.findViewById(R.id.mapView);
        assertNotNull(viewById);
    }

    @Test
    public void testMapIsDisplayed() {
        onView(withId(R.id.mapView)).check(matches(isDisplayed()));
    }

    @Test
    public void whenLocationIfOffLocationOnMapIsDisable() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(new MyMap() {
            @Override
            public void setMyLocationEnabled(boolean locationEnabled) {
                sync.assertFalse(locationEnabled);
                sync.call();
            }

            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.fail();
                return null;
            }
        }));
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void whenLocationIfOnLocationOnMapIsDisable() throws Throwable {
        LocationUtil.enableLocation();
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(new MyMap() {
            @Override
            public void setMyLocationEnabled(boolean locationEnabled) {
                sync.assertTrue(locationEnabled);
                sync.call();
            }

            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.fail();
                return null;
            }
        }));
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
        LocationUtil.disableLocation();
    }

    @Test
    public void whenNoFriendsNoMarkerOnTheMap() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(new MyMap() {
            @Override
            public void setMyLocationEnabled(boolean locationEnabled) {
                sync.call();
            }

            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.fail();
                return null;
            }
        }));
        sync.waitCall(1);
        sync.assertNoFailedTests();
        sync.assertCalled(1);
    }

    @Override
    protected void setIds() {
        setIds(R.id.map_activity_drawer_layout, R.id.map_activity_nav_view);
    }
}
