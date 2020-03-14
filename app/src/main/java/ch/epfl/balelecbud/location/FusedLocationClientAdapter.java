package ch.epfl.balelecbud.location;

import android.app.Activity;
import android.app.PendingIntent;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class FusedLocationClientAdapter implements LocationClient {
    private FusedLocationProviderClient client;
    public FusedLocationClientAdapter(Activity activity) {
        this.client = LocationServices.getFusedLocationProviderClient(activity);
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
