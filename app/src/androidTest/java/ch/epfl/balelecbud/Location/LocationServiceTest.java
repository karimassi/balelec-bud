package ch.epfl.balelecbud.Location;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;

import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class LocationServiceTest {
    private final static String LOCATION_KEY = "com.google.android.gms.location.EXTRA_LOCATION_RESULT";
    private final LocationFirestore emptyLf = new LocationFirestore() {
        @Override
        public void handleGeoPoint(GeoPoint gp, OnCompleteListener<Void> callback) {
            throw new RuntimeException();
        }
    };
    private final OnCompleteListener<Void> emptyOncl = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            throw new RuntimeException();
        }
    };

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
