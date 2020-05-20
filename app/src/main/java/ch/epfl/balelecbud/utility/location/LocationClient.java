package ch.epfl.balelecbud.utility.location;

import android.app.PendingIntent;

import com.google.android.gms.location.LocationRequest;

/**
 * Interface modeling a location client
 */
public interface LocationClient {

    /**
     * Request a location updates
     *
     * @param lr     the request
     * @param intent the intent to send back
     */
    void requestLocationUpdates(LocationRequest lr, PendingIntent intent);

    /**
     * Remove a location update request
     *
     * @param intent the intent used for the request
     */
    void removeLocationUpdates(PendingIntent intent);
}
