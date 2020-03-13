package ch.epfl.balelecbud.Location;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;

import ch.epfl.balelecbud.WelcomeActivity;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
public class LocationServiceTest {
    private final static String LOCATION_KEY = "com.google.android.gms.location.EXTRA_LOCATION_RESULT";
    private final LocationFirestore emptyLf = new LocationFirestore() {
        @Override
        public void handleGeoPoint(GeoPoint gp, OnCompleteListener<Void> callback) {
           Assert.fail();
        }
    };
    private final OnCompleteListener<Void> emptyOncl = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            Assert.fail();
        }
    };

    @Rule
    public final ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule<>(WelcomeActivity.class);

    @Before
    public void grantPermission() throws IOException {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.executeShellCommand("pm reset-permissions");
        if (device.hasObject(By.text("ALLOW"))) {
            device.findObject(By.text("ALLOW")).click();
            device.waitForWindowUpdate(null, 1000);
        }
    }

    private Intent getIntent(Location l) {
        Intent i = new Intent(mActivityRule.getActivity(), LocationService.class);
        LocationResult lr = LocationResult.create(Collections.singletonList(l));
        Bundle b = new Bundle();
        b.putParcelable(LOCATION_KEY, lr);
        i.putExtras(b);
        i.setAction(LocationService.ACTION_PROCESS_UPDATES);
        return i;
    }

    @Test
    public void nullIntent() {
        LocationService ls = new LocationService(this.emptyLf, this.emptyOncl);
        ls.onHandleIntent(null);
    }

    @Test
    public void nullLocation() {
        LocationService ls = new LocationService(new LocationFirestore() {
            @Override
            public void handleGeoPoint(GeoPoint gp, OnCompleteListener<Void> callback) {
                Assert.assertThat(gp, is(new GeoPoint(0, 0)));
            }
        }, this.emptyOncl);
        ls.onHandleIntent(getIntent(null));
    }

    @Test
    public void validLocation() {
        final double mockLatitude = 1.2797677;
        final double mockLongitude = 103.8459285;

        final Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(mockLatitude);
        mockLocation.setLongitude(mockLongitude);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(1);
        mockLocation.setElapsedRealtimeNanos(123456789);

        LocationService ls = new LocationService(new LocationFirestore() {
            @Override
            public void handleGeoPoint(GeoPoint gp, OnCompleteListener<Void> callback) {
                Assert.assertThat(gp, is(new GeoPoint(mockLatitude, mockLongitude)));
            }
        }, this.emptyOncl);

        ls.onHandleIntent(getIntent(mockLocation));
    }

    @Test
    public void intentWithInvalidAction() {
        LocationService ls = new LocationService(this.emptyLf, this.emptyOncl);
        Intent intent = new Intent();
        intent.setAction("Test");
        ls.onHandleIntent(intent);
    }
}
