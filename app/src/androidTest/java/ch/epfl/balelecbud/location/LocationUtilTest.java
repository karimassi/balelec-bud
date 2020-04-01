package ch.epfl.balelecbud.location;

import android.app.PendingIntent;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.location.LocationRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.LinkedList;
import java.util.List;

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
    }

    @Test
    public void enableLocationSentToClient() throws InterruptedException {
        final List<Object> sync = new LinkedList<>();
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                Assert.fail();
            }
        });

        LocationUtil.enableLocation(ApplicationProvider.getApplicationContext());
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertEquals(1, sync.size());
        Assert.assertTrue(LocationUtil.isLocationActive(ApplicationProvider.getApplicationContext()));
    }

    @Test
    public void enableThenDisableLocationSentToClient() throws InterruptedException {
        final List<Object> sync = new LinkedList<>();
        LocationUtil.setLocationClient(new LocationClient() {
            PendingIntent intent;
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                Assert.assertNotNull(lr);
                Assert.assertNotNull(intent);
                this.intent = intent;
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                Assert.assertEquals(this.intent, intent);
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
            }
        });

        LocationUtil.enableLocation(ApplicationProvider.getApplicationContext());
        synchronized (sync) {
            sync.wait(1000);
        }
        LocationUtil.disableLocation(ApplicationProvider.getApplicationContext());
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertEquals(2, sync.size());
        Assert.assertFalse(LocationUtil.isLocationActive(ApplicationProvider.getApplicationContext()));
    }

    @Test
    public void disableLocationSentToClient() throws InterruptedException {
        final List<Object> sync = new LinkedList<>();
        LocationUtil.setLocationClient(new LocationClient() {
            @Override
            public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
                Assert.fail();
            }

            @Override
            public void removeLocationUpdates(PendingIntent intent) {
                Assert.assertNotNull(intent);
                synchronized (sync) {
                    sync.add(new Object());
                    sync.notify();
                }
            }
        });

        LocationUtil.disableLocation(ApplicationProvider.getApplicationContext());
        synchronized (sync) {
            sync.wait(1000);
        }
        Assert.assertEquals(1, sync.size());
        Assert.assertFalse(LocationUtil.isLocationActive(ApplicationProvider.getApplicationContext()));
    }

    @Test
    public void defaultConstructor() {
        new LocationUtil();
    }
}
