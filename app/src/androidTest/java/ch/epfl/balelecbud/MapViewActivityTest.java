package ch.epfl.balelecbud;

import android.view.Gravity;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
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
    private Location testLocation = new Location(testLatitude, testLongitude);
    private LatLng testLatLng = new LatLng(testLatitude, testLongitude);
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
    public void testUpdateLocationUi() throws Throwable {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MapViewActivity mActivity = mActivityRule.getActivity();
                GoogleMap googleMap = mActivity.getGoogleMap();
                if(googleMap != null) {
                    assertThat(googleMap.isMyLocationEnabled(),
                            is(MapViewActivity.getLocationPermission()));
                    assertThat(googleMap.getUiSettings().isMyLocationButtonEnabled(),
                            is(MapViewActivity.getLocationPermission()));
                }

            }
        });
    }

    @Test
    public void testNewLocationIsSet() {
        Location newLocation = new Location(30, 8);
        MapViewActivity mActivity = mActivityRule.getActivity();
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
        }
        else {
            assertNull(mActivity.getLocationResult());
        }
    }

    @Test
    public void testSetLocationPermission() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        assertThat(mActivity.getLocationPermission(), is(LocationUtil.isLocationActive(mActivity)));
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

    @Test
    public void testDrawer() {
        onView(withId(R.id.map_activity_drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.headerImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.map_activity_nav_view)).check(matches(isDisplayed()));
    }

    @Test
    public void openInfoActivityFromDrawer() {
        onView(withId(R.id.map_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.map_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.map_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_info));
        onView(withId(R.id.festivalInfoRecyclerView)).check(matches(isDisplayed()));

    }

    @Test
    public void openScheduleActivityFromDrawer() {
        onView(withId(R.id.map_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.map_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.map_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_schedule));
        onView(withId(R.id.scheduleRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openPOIActivityFromDrawer() {
        onView(withId(R.id.map_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.map_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.map_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_poi));
        onView(withId(R.id.pointOfInterestRecyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void openMapActivityFromDrawer() {
        onView(withId(R.id.map_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.map_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.map_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_map));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void openTransportActivityFromDrawer() {
        onView(withId(R.id.map_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.map_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.map_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.activity_main_drawer_transport));
        onView(withId(R.id.fragmentTransportList)).check(matches(isDisplayed()));
    }

    @Test
    public void signOutFromDrawer() {
        onView(withId(R.id.map_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.map_activity_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.map_activity_nav_view)).perform(NavigationViewActions.navigateTo(R.id.sign_out_button));
        onView(withId(R.id.editTextEmailLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextPasswordLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLoginToRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackPress(){
        onView(withId(R.id.map_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.map_activity_nav_view)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withId(R.id.map_activity_drawer_layout)).check(matches(isClosed(Gravity.LEFT)));
        Espresso.pressBack();
    }
}
