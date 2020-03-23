package ch.epfl.balelecbud;

import android.location.Location;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class MapViewActivityTest {
    @Rule
    public final ActivityTestRule<MapViewActivity> mActivityRule =
            new ActivityTestRule<>(MapViewActivity.class);

    private final double testLatitude = -12.12;
    private final double testLongitude = -77.03;
    private LatLng oldMapPosition;
    private LatLng newMapPosition;

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
    public void testNewPositionIsSet() {
        LatLng newPosition = new LatLng(testLatitude, testLongitude);
        MapViewActivity mActivity = mActivityRule.getActivity();
        mActivity.setPosition(newPosition);
        assertThat(mActivity.getPosition(), is(newPosition));
    }

    @Test
    public void testNullGeoPointIsNotSet() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        mActivity.setPositionFrom(null);
        assertThat(mActivity.getPosition(), is(notNullValue()));
    }

    @Test
    public void testNewPositionFromGeoPointIsSet() {
        LatLng newPosition = new LatLng(testLatitude, testLongitude);
        MapViewActivity mActivity = mActivityRule.getActivity();
        GeoPoint geoPoint = new GeoPoint(testLatitude, testLongitude);
        mActivity.setPositionFrom(geoPoint);
        assertThat(mActivity.getPosition(), is(newPosition));
    }

    @Test
    public void testNullPositionIsNotSet() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        mActivity.setPosition(null);
        assertThat(mActivity.getPosition(), is(notNullValue()));
    }

    @Test
    public void testNewPositionFromLocationIsSet() {
        testSetPositionFrom(newTestLocation(), true);
        assertThat(newMapPosition, is(new LatLng(testLatitude, testLongitude)));
    }

    @Test
    public void testNullLocationIsNotSet() {
        testSetPositionFrom(null, true);
        assertThat(newMapPosition, is(oldMapPosition));

        testSetPositionFrom(null, false);
        assertThat(newMapPosition, is(oldMapPosition));
    }

    @Test
    public void testLocationIsNotSetWhenLocationDisabled() {
        testSetPositionFrom(newTestLocation(), false);
        assertThat(newMapPosition, is(oldMapPosition));
    }

    private void testSetPositionFrom(final Location location, final boolean locationEnabled){
        MapViewActivity mActivity = mActivityRule.getActivity();
        oldMapPosition = mActivity.getPosition();
        mActivity.setPositionFrom(location, locationEnabled);
        newMapPosition = mActivity.getPosition();
    }

    private Location newTestLocation() {
        Location location = new Location("Test location");
        location.setLatitude(testLatitude);
        location.setLongitude(testLongitude);
        return location;
    }
}
