package ch.epfl.balelecbud.map;

import android.app.PendingIntent;

import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.location.LocationRequest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivityTest;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.location.LocationClient;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.util.database.MockDatabase;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.util.database.MockDatabase.celine;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class MapViewFragmentTest extends RootActivityTest {
    private final MockDatabase mockDB = MockDatabase.getInstance();
    private final MockAuthenticator mockAuth = MockAuthenticator.getInstance();

    @Before
    public void setup() {

        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
            }
        });

        BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
        MockAuthenticator.getInstance().setCurrentUser(celine);
        BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
    }


    @Override
    protected int getItemId() {
        return R.id.activity_main_drawer_map;
    }

    @Override
    protected int getViewToDisplayId() {
        return R.id.activity_map_linear_layout;
    }

    @Ignore("Should get back to this")
    @Test
    public void testMapViewIsNotNull() {
//        Activity mActivity = super.mActivityRule.getActivity();
//        View viewById = mActivity.findViewById(R.id.map_view);
//        assertNotNull(viewById);
    }

    @Test
    public void testMapIsDisplayed() {
        FragmentScenario.launchInContainer(MapViewFragment.class);
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    @Test
    public void whenLocationIsOffLocationOnMapIsDisabled() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();

        MapViewFragment.setMockMap(assertMapLocation(sync, false));
        FragmentScenario.launchInContainer(MapViewFragment.class);

        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void whenLocationIfOnLocationOnMapIsDisable() throws Throwable {
        LocationUtil.enableLocation();
        TestAsyncUtils sync = new TestAsyncUtils();

        MapViewFragment.setMockMap(assertMapLocation(sync, true));
        FragmentScenario.launchInContainer(MapViewFragment.class);

        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
        LocationUtil.disableLocation();
    }

    @NonNull
    private MyMap assertMapLocation(TestAsyncUtils sync, boolean expectedLocation) {
        return new MyMap() {
            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.fail();
                return null;
            }

            @Override
            public void initialiseMap(boolean locationEnabled, Location defaultLocation) {
                sync.assertEquals(expectedLocation, locationEnabled);
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
            public void initialiseMap(boolean locationEnabled, Location defaultLocation) {
                sync.call();
            }
        };

        MapViewFragment.setMockMap(mockMap);
        FragmentScenario.launchInContainer(MapViewFragment.class);

        sync.waitCall(1);
        sync.assertNoFailedTests();
        sync.assertCalled(1);
    }

    @Ignore("Should get back to this")
    @Test
    public void testOnLowMemory() throws Throwable {
//        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onLowMemory());
    }
}