package ch.epfl.balelecbud.map;

import android.location.Location;

public interface MyLocationComponent {

    void setLocationComponentEnabled(boolean state);

    Location getLastKnownLocation();

}
