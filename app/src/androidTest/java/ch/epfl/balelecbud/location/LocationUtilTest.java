package ch.epfl.balelecbud.location;

import android.app.PendingIntent;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.location.LocationRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;

@RunWith(JUnit4.class)
public class LocationUtilTest {
    @Before
    public void setDummyLocationClient() {
        LocationUtil.setLocationClient(new LocationClient() {
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
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                sync.call();
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                sync.fail();
            }
        });

        LocationUtil.enableLocation();
        sync.assertCalled(1);
        sync.assertNoFailedTests();
        Assert.assertTrue(LocationUtil.isLocationActive());
    }

    @Test
    public void enableThenDisableLocationSentToClient() {
        final TestAsyncUtils sync = new TestAsyncUtils();
        LocationUtil.setLocationClient(new LocationClient() {
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
        LocationUtil.enableLocation();
        LocationUtil.disableLocation();
        sync.assertCalled(2);
        sync.assertNoFailedTests();
        Assert.assertFalse(LocationUtil.isLocationActive());
    }

    @Test
    public void disableLocationSentToClient() {
        final TestAsyncUtils sync = new TestAsyncUtils();
        LocationUtil.setLocationClient(new LocationClient() {
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

        LocationUtil.disableLocation();
        sync.assertCalled(1);
        sync.assertNoFailedTests();
        Assert.assertFalse(LocationUtil.isLocationActive());
    }

    @Test
    public void defaultConstructor() {
        new LocationUtil();
    }
}
