package ch.epfl.balelecbud.view;

import androidx.fragment.app.Fragment;

import java.util.Collections;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.utility.database.query.MyQuery;

public abstract class ConnectivityFragment extends Fragment {
    public abstract String collectionName();
    public boolean canBeDisplayed() {
        return BalelecbudApplication.getConnectivityChecker().isConnectionAvailable() ||
                BalelecbudApplication.getAppCache().contains(new MyQuery(collectionName(), Collections.emptyList()));
    }
}
