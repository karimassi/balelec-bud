package ch.epfl.balelecbud;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.models.User;
import ch.epfl.balelecbud.transport.objects.TransportStation;

@RunWith(AndroidJUnit4.class)
public class ParcelableTest {

    private Parcel parcel;

    @Before
    public void setup() {
        parcel = Parcel.obtain();
    }

    private <T extends Parcelable> void writeToParcel(T o) {
        o.writeToParcel(parcel, o.describeContents());
        parcel.setDataPosition(0);
    }

    @Test
    public void testLocationIsParcelable() {
        Location location = new Location(48.0, 6.0);
        writeToParcel(location);
        Location fromParcel = Location.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(location, fromParcel);
    }

    @Test
    public void testUserIsParcelable() {
        User user = new User("email", "user", "0");
        writeToParcel(user);
        User fromParcel = User.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(user, fromParcel);
    }

    @Test
    public void testTransportStationIsParcelable() {
        TransportStation station = new TransportStation(Location.DEFAULT_LOCATION, "0", "name", 1);
        writeToParcel(station);
        TransportStation fromParcel = TransportStation.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(station, fromParcel);
    }

}
