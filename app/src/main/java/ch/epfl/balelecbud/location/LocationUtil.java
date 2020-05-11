package ch.epfl.balelecbud.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.LocationRequest;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.util.database.MyGeoClause;

public final class LocationUtil {

    private final static String TAG = LocationUtil.class.getSimpleName();
    private final static double EARTH_RADIUS = 6371;
    private final static long FASTEST_UPDATE_INTERVAL = 30_000;
    public final static int LOCATION_PERMISSIONS_REQUEST_CODE = 34;
    private final static long UPDATE_INTERVAL = 60_000;
    private final static long MAX_WAIT_TIME = UPDATE_INTERVAL;
    private static LocationRequest locationRequest;
    private static LocationClient client;

    private static void createLocationRequest() {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(LocationUtil.UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LocationUtil.FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(LocationUtil.MAX_WAIT_TIME);
    }

    @VisibleForTesting
    public static void setLocationClient(LocationClient client) {
        LocationUtil.client = client;
    }

    private static LocationClient getClient(Context context) {
        if (LocationUtil.client == null)
            LocationUtil.client = new FusedLocationClientAdapter(context);
        return LocationUtil.client;
    }

    private static LocationRequest getLocationRequestInstance() {
        if (locationRequest == null)
            createLocationRequest();
        return locationRequest;
    }

    /**
     * Request the permissions required for the location service
     *
     * @param fragment the fragment requesting the permissions
     */
    public static void requestLocationPermission(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fragment.requestPermissions(
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE);
        } else {
            fragment.requestPermissions(
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION },
                    LocationUtil.LOCATION_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Handle the result of the request for permissions needed for the location service
     *
     * @param grantResults         the results of the request
     * @param onPermissionCanceled an action to perform if the permission request is canceled
     * @param onPermissionGranted  an action to perform if the permission request is granted
     * @param onPermissionDenied   an action to perform if the permission request is denied
     */
    public static void onLocationRequestPermissionsResult(@NonNull int[] grantResults,
                                                          @NonNull Runnable onPermissionCanceled,
                                                          @NonNull Runnable onPermissionGranted,
                                                          @NonNull Runnable onPermissionDenied) {
        if (grantResults.length <= 0) {
            onPermissionCanceled.run();
        } else if (isLocationPermissionGranted(grantResults)) {
            onPermissionGranted.run();
        } else {
            onPermissionDenied.run();
        }
    }

    private static boolean isLocationPermissionGranted(@NonNull int[] grantResults) {
        return (grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                        grantResults[1] == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Check if the location service is activated or not
     *
     * @return the location service status
     */
    public static boolean isLocationActive() {
        Context ctx = BalelecbudApplication.getAppContext();
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(ctx.getString(R.string.location_enable_key), false);
    }

    /**
     * Enable the location service
     */
    public static void enableLocation() {
        changedLocationState(true);
    }

    /**
     * Disable the location service
     */
    public static void disableLocation() {
        changedLocationState(false);
    }

    private static void changedLocationState(boolean status) {
        Log.d(TAG, "changedLocationState() called with: status = [" + status + "]");
        updateLocation(status);
        Context ctx = BalelecbudApplication.getAppContext();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        editor.putBoolean(ctx.getString(R.string.location_enable_key), status);
        editor.apply();
    }

    public static void updateLocation(boolean status) {
        Context ctx = BalelecbudApplication.getAppContext();
        if (status) {
            requestLocationUpdates(ctx);
        } else {
            removeLocationUpdates(ctx);
        }
    }

    /**
     * Compute distance between two points in latitude and longitude.
     *
     * lat1, lon1 Start point lat2, lon2 End point
     * @returns Distance in km
     *
     * Borrowed from https://stackoverflow.com/a/16794680
     */
    public static double distanceToCenter(Location centerLocation, Location location) {
        double lat1 = centerLocation.getLatitude();
        double lon1 = centerLocation.getLongitude();
        double lat2 = location.getLatitude();
        double lon2 = location.getLongitude();
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(LocationService.ACTION_PROCESS_UPDATES);
        return PendingIntent.getService(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static void requestLocationUpdates(Context context) {
        try {
            Log.i(TAG, "Starting location updates");
            getClient(context).requestLocationUpdates(getLocationRequestInstance(), getPendingIntent(context));
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private static void removeLocationUpdates(Context context) {
        Log.i(TAG, "Removing location updates");
        getClient(context).removeLocationUpdates(getPendingIntent(context));
    }
}
