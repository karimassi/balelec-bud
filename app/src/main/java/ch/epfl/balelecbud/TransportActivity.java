package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import ch.epfl.balelecbud.transport.TransportListFragment;
import ch.epfl.balelecbud.transport.objects.Transport;

public class TransportActivity extends FragmentActivity implements TransportListFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
    }

    @Override
    public void onListFragmentInteraction(Transport item) {
        //do nothing for now
    }
}
