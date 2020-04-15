package ch.epfl.balelecbud.map;

import android.app.PendingIntent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.location.LocationRequest;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

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
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class MyMarkerBuildTest extends BasicActivityTest {
    private final Location location = new Location(1, 2);
    private final String title = "abcd";
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
                    MapViewActivity.setMockCallback(mapboxMap -> {});
                    LocationUtil.setLocationClient(new LocationClient() {
                        @Override
                        public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) { }

                        @Override
                        public void removeLocationUpdates(PendingIntent intent) { }
                    });
                    mockAuth.setCurrentUser(MockDatabaseWrapper.celine);
                }
            };

    @Test
    public void canSetLocation() {
        assertEquals(location, new MyMarker.Builder().location(location).getLocation());
    }

    @Test
    public void canSetTitle() {
        assertEquals(title, new MyMarker.Builder().title(title).getTitle());
    }

    @Test
    public void canSetIcon() {
        assertNull(new MyMarker.Builder().icon(null).getIcon());
    }

    @Test
    public void canConvertToGoogleMarkerOption() {
        MarkerOptions markerOptions =
                new MyMarker.Builder().
                        title(title).
                        location(location).
                        toMapboxMarkerOptions();
        assertEquals(title, markerOptions.getTitle());
        assertEquals(new LatLng(location.getLatitude(), location.getLongitude()), markerOptions.getPosition());
    }

    @Override
    protected void setIds() {
        setIds(R.id.map_activity_drawer_layout, R.id.map_activity_nav_view);
    }
}
