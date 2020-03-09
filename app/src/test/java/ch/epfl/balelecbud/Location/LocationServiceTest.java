package ch.epfl.balelecbud.Location;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.location.LocationResult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;

@RunWith(JUnit4.class)
public class LocationServiceTest {
    private final static String LOCATION_KEY = "com.google.android.gms.location.EXTRA_LOCATION_RESULT";

    private Intent getIntent(Location l) {
        Intent i = new Intent();
        i.setAction(LocationService.ACTION_PROCESS_UPDATES);
        LocationResult lr = LocationResult.create(Collections.singletonList(l));
        Bundle b = new Bundle();
        b.putParcelable(LOCATION_KEY, lr);
        i.putExtras(b);
        return i;
    }

    @Test
    public void nullIntent() {
        LocationService ls = new LocationService(new LocationFirestore() {
            @Override
            public void handleLocation(Location l) {
                throw new RuntimeException();
            }
        });
        ls.onHandleIntent(null);
    }

    @Test
    public void nullLocation() {
        LocationService ls = new LocationService(new LocationFirestore() {
            @Override
            public void handleLocation(Location l) {
                Assert.assertNull(l);
            }
        });
        ls.onHandleIntent(getIntent(null));
    }

    @Test
    public void validLocation() {
        final Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(1.2797677);
        mockLocation.setLongitude(103.8459285);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(1);
        mockLocation.setElapsedRealtimeNanos(123456789);

        LocationService ls = new LocationService(new LocationFirestore() {
            @Override
            public void handleLocation(Location l) {
                Assert.assertEquals(mockLocation, l);
            }
        });

        ls.onHandleIntent(getIntent(mockLocation));
    }

    @Test
    public void intentWithInvalidAction() {
        LocationService ls = new LocationService(new LocationFirestore() {
            @Override
            public void handleLocation(Location l) {
                throw new RuntimeException();
            }
        });
        Intent intent = new Intent();
        intent.setAction("Test");
        ls.onHandleIntent(intent);
    }
}
