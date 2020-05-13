package ch.epfl.balelecbud.view.transport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Location;
import ch.epfl.balelecbud.model.TransportStation;
import ch.epfl.balelecbud.utility.recyclerViews.OnRecyclerViewInteractionListener;

public class TransportFragment extends Fragment implements OnRecyclerViewInteractionListener<TransportStation> {

    private FragmentActivity activity;

    public static TransportFragment newInstance() {
        return (new TransportFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_transport, container, false);
    }

    public void switchFragment(Fragment fragment) {
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.transport_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStart(){
        super.onStart();
        activity = getActivity();
        TransportStationsFragment fragment = TransportStationsFragment.newInstance(Location.DEFAULT_LOCATION);
        fragment.setInteractionListener(this);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.transport_fragment_container, fragment)
                .setPrimaryNavigationFragment(fragment)
                .commit();
    }

    @Override
    public void onItemSelected(TransportStation item) {
        switchFragment(TransportDeparturesFragment.newInstance(item));
    }
}
