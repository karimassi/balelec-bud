package ch.epfl.balelecbud.view;

import androidx.fragment.app.Fragment;

import java.util.Collections;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.utility.database.query.MyQuery;

/**
 * A special Fragment that defines a function {@code canBeDisplayed} that tells if this fragment can be displayed
 * i.e. it has the information it displays in the cache or there is a working internet connection
 */
public abstract class ConnectivityFragment extends Fragment {
    /**
     * By default it return {@code null}. Override this function if you want to check the cache contents
     *
     * @return the name of the collection this fragment needs
     */
    public String collectionName() {
        return null;
    }

    /**
     * Tells if this fragment can be display. By default it only checks if there is a working internet connection.
     * Override the {@code collectionName} function to check inside the cache as well.
     *
     * @return if this fragment can be displayed
     */
    public final boolean canBeDisplayed() {
        if (collectionName() == null) {
            return BalelecbudApplication.getConnectivityChecker().isConnectionAvailable();
        } else {
            return BalelecbudApplication.getConnectivityChecker().isConnectionAvailable() ||
                    BalelecbudApplication.getAppCache().contains(new MyQuery(collectionName(), Collections.emptyList()));
        }
    }
}
