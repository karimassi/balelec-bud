package ch.epfl.balelecbud.map;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;
import ch.epfl.balelecbud.RootActivityTest;
import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.balelecbud.testUtils.TestAsyncUtils.runOnUIThreadAndWait;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class MapViewFragmentTest extends RootActivityTest {

    @Test
    public void testMapViewIsNotNull() {
        RootActivity mActivity = mActivityRule.getActivity();
        View viewById = mActivity.findViewById(R.id.map_view);
        assertNotNull(viewById);
    }

    @Test
    public void testMapIsDisplayed() {
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    @Test
    public void whenLocationIsOffLocationOnMapIsDisabled() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(assertMapLocation(sync, false)));
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void whenLocationIfOnLocationOnMapIsDisable() throws Throwable {
        LocationUtil.enableLocation();
        TestAsyncUtils sync = new TestAsyncUtils();
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(assertMapLocation(sync, true)));
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
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onMapReady(new MyMap() {
            @Override
            public MyMarker addMarker(MyMarker.Builder markerBuilder) {
                sync.fail();
                return null;
            }

            @Override
            public void initialiseMap(boolean locationEnabled, Location defaultLocation) {
                sync.call();
            }
        }));
        sync.waitCall(1);
        sync.assertNoFailedTests();
        sync.assertCalled(1);
    }

    @Test
    public void testOnLowMemory() throws Throwable {
        runOnUIThreadAndWait(() -> this.mActivityRule.getActivity().onLowMemory());
    }

    @Override
    protected int getItemId() {
        return R.id.activity_main_drawer_map;
    }

    @Override
    protected int getViewToDisplayId() {
        return R.id.map_view;
    }
}
