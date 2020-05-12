package ch.epfl.balelecbud.utility.location;

import android.app.PendingIntent;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.location.LocationRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;

import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class LocationUtilsTest {
    @Before
    public void setDummyLocationClient() {
        LocationUtils.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {

            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {

            }
        });
        BalelecbudApplication.setAppContext(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void enableLocationSentToClient() {
        final TestAsyncUtils sync = new TestAsyncUtils();
        LocationUtils.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                sync.call();
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                sync.fail();
            }
        });

        LocationUtils.enableLocation();
        sync.assertCalled(1);
        sync.assertNoFailedTests();
        Assert.assertTrue(LocationUtils.isLocationActive());
    }

    @Test
    public void enableThenDisableLocationSentToClient() {
        final TestAsyncUtils sync = new TestAsyncUtils();
        LocationUtils.setLocationClient(new LocationClient() {
            PendingIntent intent;
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                sync.assertNotNull(lr);
                sync.assertNotNull(intent);
                this.intent = intent;
                sync.call();
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                sync.assertEquals(this.intent, intent);
                sync.call();
            }
        });
        LocationUtils.enableLocation();
        LocationUtils.disableLocation();
        sync.assertCalled(2);
        sync.assertNoFailedTests();
        Assert.assertFalse(LocationUtils.isLocationActive());
    }

    @Test
    public void disableLocationSentToClient() {
        final TestAsyncUtils sync = new TestAsyncUtils();
        LocationUtils.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                sync.fail();
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                sync.assertNotNull(intent);
                sync.call();
            }
        });

        LocationUtils.disableLocation();
        sync.assertCalled(1);
        sync.assertNoFailedTests();
        Assert.assertFalse(LocationUtils.isLocationActive());
    }

    @Test
    public void testDistanceToCenter() {
        Location l1 = new Location(70, 8);
        Location l2 = new Location(44, 2);
        Assert.assertThat(LocationUtils.distanceToCenter(l1, l2), is(2910.594395722676));
    }

    @Test
    public void defaultConstructor() {
        new LocationUtils();
    }
}
