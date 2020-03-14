package ch.epfl.balelecbud.location;

import android.app.PendingIntent;

import com.google.android.gms.location.LocationRequest;

public interface LocationClient {
    void requestLocationUpdates(LocationRequest lr, PendingIntent intent);
    void removeLocationUpdates(PendingIntent intent);
}
