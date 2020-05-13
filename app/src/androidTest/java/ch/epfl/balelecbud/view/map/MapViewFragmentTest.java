package ch.epfl.balelecbud.view.map;

import android.app.Activity;
import android.app.PendingIntent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.location.LocationRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.MyMap;
import ch.epfl.balelecbud.model.MyMarker;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.location.LocationClient;
import ch.epfl.balelecbud.utility.location.LocationUtils;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.utility.database.MockDatabase.camille;
import static ch.epfl.balelecbud.utility.database.MockDatabase.pointOfInterest1;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class MapViewFragmentTest {

    @Before
    public void setup() {
        LocationUtils.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
            }
        });
        MockDatabase.getInstance().resetDatabase();
        BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
        MockAuthenticator.getInstance().setCurrentUser(camille);
        BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
        MapViewFragment.setMockCallback(mapboxMap -> { });
    }

    @Test
    public void testMapViewIsNotNull() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        FragmentScenario.launchInContainer(MapViewFragment.class).onFragment(fragment -> {
            Activity mActivity = fragment.getActivity();
            View viewById = mActivity.findViewById(R.id.map_view);
            sync.assertNotNull(viewById);
            sync.call();
        });
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void testMapIsDisplayed() {
        FragmentScenario.launchInContainer(MapViewFragment.class);
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    @Test
    public void whenLocationIsOffLocationOnMapIsDisabled() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();

        FragmentScenario<MapViewFragment> scenario = FragmentScenario.launchInContainer(MapViewFragment.class);

        scenario.onFragment(fragment -> fragment.onMapReady(assertMapLocation(sync, false,
                getApplicationContext().getResources().getInteger(R.integer.default_zoom))));

        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void whenLocationIsOnLocationOnMapIsEnabled() throws Throwable {
        LocationUtils.enableLocation();
        TestAsyncUtils sync = new TestAsyncUtils();

        FragmentScenario<MapViewFragment> scenario = FragmentScenario.launchInContainer(MapViewFragment.class);
        scenario.onFragment(fragment -> fragment.onMapReady(assertMapLocation(sync, true,
                getApplicationContext().getResources().getInteger(R.integer.default_zoom))));

        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
        LocationUtils.disableLocation();
    }

    @NonNull
    private MyMap assertMapLocation(TestAsyncUtils sync, boolean expectedLocation, double expectedZoom) {
        return new MyMap() {
            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.fail();
                return null;
            }

            @Override
            public void initialiseMap(boolean locationEnabled, Location defaultLocation, double zoom) {
                sync.assertEquals(expectedLocation, locationEnabled);
                sync.assertEquals(expectedZoom, zoom);
                sync.assertThat(defaultLocation, is(Location.DEFAULT_LOCATION));
                sync.call();
            }
        };
    }

    @Test
    public void whenNoFriendsNoMarkerOnTheMap() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();

        MyMap mockMap = new MyMap() {
            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.fail();
                return null;
            }

            @Override
            public void initialiseMap(boolean locationEnabled, Location defaultLocation, double zoom) {
                sync.call();
            }
        };

        FragmentScenario<MapViewFragment> scenario = FragmentScenario.launchInContainer(MapViewFragment.class);
        scenario.onFragment(fragment -> fragment.onMapReady(mockMap));

        sync.waitCall(1);
        sync.assertNoFailedTests();
        sync.assertCalled(1);
    }

    @Test
    public void testOpenMapWithPOILocationInBundle() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        MyMap mockMap = new MyMap() {
            @Override
            public void initialiseMap(boolean appLocationEnabled, Location defaultLocation, double zoom) {
                sync.assertThat(zoom, is((double) getApplicationContext().getResources().getInteger(R.integer.poi_zoom)));
                sync.assertThat(defaultLocation, is(pointOfInterest1.getLocation()));
                sync.call();
            }

            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                return null;
            }
        };
        Bundle bundle = new Bundle();
        bundle.putParcelable("location", pointOfInterest1.getLocation());
        FragmentScenario<MapViewFragment> scenario = FragmentScenario.launchInContainer(MapViewFragment.class, bundle);
        scenario.onFragment(fragment -> fragment.onMapReady(mockMap));

        sync.waitCall(1);
        sync.assertNoFailedTests();
        sync.assertCalled(1);
    }

    @Test
    public void testOnLowMemory() {
        FragmentScenario.launchInContainer(MapViewFragment.class).onFragment(fragment -> {
            Activity mActivity = fragment.getActivity();
            mActivity.onLowMemory();
        });
    }
}