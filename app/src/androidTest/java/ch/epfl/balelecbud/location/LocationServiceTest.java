package ch.epfl.balelecbud.location;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.location.LocationResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.authentication.Authenticator;
import ch.epfl.balelecbud.authentication.MockAuthenticator;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.MockDatabaseWrapper;

@RunWith(AndroidJUnit4.class)
public class LocationServiceTest {
    private final static String LOCATION_KEY = "com.google.android.gms.location.EXTRA_LOCATION_RESULT";
    private LocationService ls;
    private final Authenticator mockAuth = MockAuthenticator.getInstance();
    private final DatabaseWrapper mockDB = MockDatabaseWrapper.getInstance();
    private final Random random = new Random(42);

    @BeforeClass
    public static void setUpMock() {
        BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
        BalelecbudApplication.setAppDatabaseWrapper(MockDatabaseWrapper.getInstance());
    }

    @Before
    public void setUp() {
        ls = new LocationService();
        mockAuth.setCurrentUser(new User("abc@epfl.ch", "abc", "10"));
        ls.onCreate();
    }

    private Intent getIntent(android.location.Location l) {
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), LocationService.class);
        LocationResult lr = LocationResult.create(Collections.singletonList(l));
        Bundle b = new Bundle();
        b.putParcelable(LOCATION_KEY, lr);
        i.putExtras(b);
        i.setAction(LocationService.ACTION_PROCESS_UPDATES);
        return i;
    }

    private void checkDoesNotChangeOnDBWithIntent(Intent intent) throws ExecutionException, InterruptedException {
        Location l = new Location(random.nextDouble(), random.nextDouble());
        mockDB.storeDocumentWithID(DatabaseWrapper.LOCATIONS_PATH, mockAuth.getCurrentUser().getUid(), l);
        ls.onHandleIntent(intent);
        checkStoredOnDB(l);
    }

    private void checkStoredOnDB(Location location) throws ExecutionException, InterruptedException {
        Assert.assertEquals(
                location,
                mockDB.getCustomDocument(
                        DatabaseWrapper.LOCATIONS_PATH,
                        mockAuth.getCurrentUser().getUid(),
                        Location.class).get()
        );
    }

    @Test
    public void intentWithInvalidAction() throws ExecutionException, InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LocationService.class);
        intent.setAction("Test");
        checkDoesNotChangeOnDBWithIntent(intent);
    }

    @Test
    public void nullIntent() throws ExecutionException, InterruptedException {
        checkDoesNotChangeOnDBWithIntent(null);
    }

    @Test
    public void nullLocation() throws ExecutionException, InterruptedException {
        checkDoesNotChangeOnDBWithIntent(getIntent(null));
    }

    private android.location.Location generateAndroidLocation(double lat, double lon) {
        final android.location.Location mockLocation = new android.location.Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(lat);
        mockLocation.setLongitude(lon);
        mockLocation.setAltitude(random.nextDouble());
        mockLocation.setTime(random.nextLong());
        mockLocation.setAccuracy(random.nextFloat());
        mockLocation.setElapsedRealtimeNanos(random.nextLong());
        return mockLocation;
    }

    @Test
    public void validLocation() throws ExecutionException, InterruptedException {
        android.location.Location location = generateAndroidLocation(24, 42);

        ls.onHandleIntent(getIntent(location));
        checkStoredOnDB(new Location(location.getLatitude(), location.getLongitude()));
    }

    @Test
    public void twoValidLocations() throws ExecutionException, InterruptedException {
        android.location.Location location1 = generateAndroidLocation(12, 34);

        ls.onHandleIntent(getIntent(location1));
        checkStoredOnDB(new Location(location1.getLatitude(), location1.getLongitude()));

        android.location.Location location2 = generateAndroidLocation(13.4, 54.2);

        ls.onHandleIntent(getIntent(location2));
        checkStoredOnDB(new Location(location2.getLatitude(), location2.getLongitude()));
    }
}
