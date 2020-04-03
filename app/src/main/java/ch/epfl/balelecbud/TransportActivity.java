package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.LocationServices;

import ch.epfl.balelecbud.location.LocationUtil;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.transport.TransportDeparturesFragment;
import ch.epfl.balelecbud.transport.TransportStationsFragment;
import ch.epfl.balelecbud.transport.objects.TransportStation;
import ch.epfl.balelecbud.util.views.OnRecyclerViewInteractionListener;

public class TransportActivity extends BasicActivity implements OnRecyclerViewInteractionListener<TransportStation> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
        TransportStationsFragment fragment = TransportStationsFragment.newInstance(Location.DEFAULT_LOCATION);
        fragment.setInteractionListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.transport_fragment_container, fragment)
                .commit();
    }

    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.transport_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onItemSelected(TransportStation item) {

        switchFragment(TransportDeparturesFragment.newInstance(item));
    }

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            NavUtils.navigateUpFromSameTask(this);
        }
    }
}
