package ch.epfl.balelecbud;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import ch.epfl.balelecbud.emergency.EmergencyInfoListFragment;
import ch.epfl.balelecbud.emergency.models.EmergencyInfo;

public class EmergencyInfoActivity extends FragmentActivity implements EmergencyInfoListFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_info);
    }

    @Override
    public void onListFragmentInteraction(EmergencyInfo item) {
        //do nothing for now
    }
}
