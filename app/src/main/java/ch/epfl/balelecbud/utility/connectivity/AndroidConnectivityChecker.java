package ch.epfl.balelecbud.utility.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ch.epfl.balelecbud.BalelecbudApplication;

public class AndroidConnectivityChecker implements ConnectivityChecker {

    private static final AndroidConnectivityChecker instance = new AndroidConnectivityChecker();

    private AndroidConnectivityChecker() {}

    public static ConnectivityChecker getInstance(){
        return instance;
    }

    /*
     * Preferred way to check internet connection as per
     * https://developer.android.com/training/monitoring-device-state/connectivity-status-type?authuser=1
     */
    @Override
    public boolean isConnectionAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) BalelecbudApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }
}
