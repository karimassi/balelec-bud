package ch.epfl.balelecbud.utility.location;

import android.app.PendingIntent;
import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * A Google's FusedLocationClient adapter
 */
public final class FusedLocationClientAdapter implements LocationClient {
    private final FusedLocationProviderClient client;

    FusedLocationClientAdapter(Context context) {
        this.client = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public void requestLocationUpdates(LocationRequest lr, PendingIntent intent) {
        this.client.requestLocationUpdates(lr, intent);
    }

    @Override
    public void removeLocationUpdates(PendingIntent intent) {
        this.client.removeLocationUpdates(intent);
    }
}
