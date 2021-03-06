package ch.epfl.balelecbud.utility.location;

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
import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.utility.authentication.Authenticator;
import ch.epfl.balelecbud.utility.authentication.MockAuthenticator;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.MockDatabase;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.database.query.MyWhereClause;

import static ch.epfl.balelecbud.utility.database.Database.DOCUMENT_ID_OPERAND;
import static ch.epfl.balelecbud.utility.database.query.MyWhereClause.Operator.EQUAL;

@RunWith(AndroidJUnit4.class)
public class LocationServiceTest {
    private final static String LOCATION_KEY = "com.google.android.gms.location.EXTRA_LOCATION_RESULT";
    private LocationService ls;
    private final Authenticator mockAuth = MockAuthenticator.getInstance();
    private final MockDatabase mockDB = MockDatabase.getInstance();
    private final Random random = new Random(42);

    @BeforeClass
    public static void setUpMock() {
        BalelecbudApplication.setAppAuthenticator(MockAuthenticator.getInstance());
        BalelecbudApplication.setAppDatabase(MockDatabase.getInstance());
    }

    @Before
    public void setUp() {
        mockDB.resetDatabase();
        ls = new LocationService();
        mockAuth.signInAnonymously();
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
        mockDB.storeDocumentWithID(Database.LOCATIONS_PATH, mockAuth.getCurrentUid(), l);
        ls.onHandleIntent(intent);
        checkStoredOnDB(l);
    }

    private void checkStoredOnDB(Location location) throws ExecutionException, InterruptedException {
        MyQuery query = new MyQuery(Database.LOCATIONS_PATH, new MyWhereClause(DOCUMENT_ID_OPERAND, EQUAL, mockAuth.getCurrentUid()));
        Assert.assertEquals(
                location,
                mockDB.query(query, Location.class).thenApply(locations -> locations.getList().get(0)).get()
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
