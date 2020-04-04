package ch.epfl.balelecbud;

import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class MapViewActivityTest {
    @Rule
    public final ActivityTestRule<MapViewActivity> mActivityRule =
            new ActivityTestRule<>(MapViewActivity.class);

    private final double testLatitude = -12.12;
    private final double testLongitude = -77.03;
    private final Location testLocation = new Location(testLatitude, testLongitude);
    private final LatLng testLatLng = new LatLng(testLatitude, testLongitude);
    private Location oldMapLocation;
    private Location newMapLocation;

    @Test
    public void testMapViewIsNotNull() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        View viewById = mActivity.findViewById(R.id.map);
        assertThat(viewById, notNullValue());
    }

    @Test
    public void testMapIsDisplayed() {
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testMapButtonIsNotDisplayed() {
        onView(withId((R.id.mapButton))).check(doesNotExist());
    }

    @Test
    public void testUpdateLocationUi() throws Throwable {
        MapViewActivity mActivity = mActivityRule.getActivity();
        TestAsyncUtils sync = new TestAsyncUtils();
        TestAsyncUtils.runOnUIThreadAndWait(() -> {
            GoogleMap googleMap = mActivity.getGoogleMap();
            if(googleMap != null) {
                sync.assertThat(googleMap.isMyLocationEnabled(),
                        is(MapViewActivity.getLocationPermission()));
                sync.assertThat(googleMap.getUiSettings().isMyLocationButtonEnabled(),
                        is(MapViewActivity.getLocationPermission()));
            }
            sync.call();
        });
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void testNewLocationIsSet() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        Location newLocation = new Location(30, 8);
        mActivity.setLocation(newLocation);
        assertThat(mActivity.getLocation(), is(newLocation));
    }

    @Test
    public void testNullLocationIsNotSet() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        mActivity.setLocation(null);
        assertThat(mActivity.getLocation(), is(notNullValue()));
    }

    @Test
    public void testNewLocationFromDeviceLocationIsSet() {
        testSetLocationFrom(newTestLocation(), true);
        assertThat(newMapLocation, is(new Location(testLatitude, testLongitude)));
    }

    @Test
    public void testNullDeviceLocationIsNotSet() {
        testSetLocationFrom(null, true);
        assertThat(newMapLocation, is(oldMapLocation));

        testSetLocationFrom(null, false);
        assertThat(newMapLocation, is(oldMapLocation));
    }

    @Test
    public void testDeviceLocationNotSetWhenLocationDisabled() {
        testSetLocationFrom(newTestLocation(), false);
        assertThat(newMapLocation, is(oldMapLocation));
    }

    @Test
    public void testGetDeviceLocation() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        if(MapViewActivity.getLocationPermission()) {
            assertThat(mActivity.getLocationResult(), is(notNullValue()));
        } else {
            assertNull(mActivity.getLocationResult());
        }
    }

    @Test
    public void testSetLocationPermission() {
        assertThat(MapViewActivity.getLocationPermission(), is(LocationUtil.isLocationActive()));
    }

    @Test
    public void testCanGetLatLng() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        assertThat(mActivity.getLatLng(testLocation), is(testLatLng));
    }

    @Test
    public void testCantGetNullLatLng() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        assertNull(mActivity.getLatLng(null));
    }

    @Test
    public void testNewLatLngIsSet() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        mActivity.setLocationFrom(testLatLng);
        assertThat(mActivity.getLocation(), is(testLocation));
    }

    @Test
    public void testNullLatLngIsNotSet() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        oldMapLocation = mActivity.getLocation();
        mActivity.setLocationFrom(null);
        newMapLocation = mActivity.getLocation();
        assertThat(newMapLocation, is(oldMapLocation));
    }

    private void testSetLocationFrom(final android.location.Location deviceLocation, final boolean locationEnabled){
        MapViewActivity mActivity = mActivityRule.getActivity();
        oldMapLocation = mActivity.getLocation();
        mActivity.setLocationFrom(deviceLocation, locationEnabled);
        newMapLocation = mActivity.getLocation();
    }

    private android.location.Location newTestLocation() {
        android.location.Location deviceLocation = new android.location.Location("Test location");
        deviceLocation.setLatitude(testLatitude);
        deviceLocation.setLongitude(testLongitude);
        return deviceLocation;
    }
}
