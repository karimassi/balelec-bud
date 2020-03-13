package ch.epfl.balelecbud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import ch.epfl.balelecbud.Transport.Object.Transport;
import ch.epfl.balelecbud.Transport.TransportListFragment;
import ch.epfl.balelecbud.Transport.TransportListener;

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
