package ch.epfl.balelecbud;

import android.location.Location;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class MapViewActivityTest {
    @Rule
    public final ActivityTestRule<MapViewActivity> mActivityRule =
            new ActivityTestRule<>(MapViewActivity.class);

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
    public void testNewPositionIsSet() {
        LatLng newPosition = new LatLng(30, 8);
        MapViewActivity mActivity = mActivityRule.getActivity();
        mActivity.setPosition(newPosition);
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
        MapViewActivity mActivity = mActivityRule.getActivity();
        double latitude = -12.12;
        double longitude = -77.03;
        LatLng newPosition = new LatLng(latitude, longitude);
        Location location = new Location("Test location");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        mActivity.setPositionFrom(location);
        assertThat(mActivity.getPosition(), is(newPosition));
    }

    @Test
    public void testNullLocationIsNotSet() {
        MapViewActivity mActivity = mActivityRule.getActivity();
        mActivity.setPositionFrom(null);
        assertThat(mActivity.getPosition(), is(notNullValue()));
    }
}
