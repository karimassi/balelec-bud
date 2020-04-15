package ch.epfl.balelecbud.map;

import android.location.Location;

import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import ch.epfl.balelecbud.BalelecbudApplication;

public class MapboxLocationComponentAdapter implements MyLocationComponent{

    private LocationComponent locationComponent;

    public MapboxLocationComponentAdapter(MapboxMap mapboxMap, boolean enabled) {
        locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(BalelecbudApplication.getAppContext(), mapboxMap.getStyle()).build());
        setLocationComponentEnabled(enabled);
    }

    @Override
    public void setLocationComponentEnabled(boolean state) {
        locationComponent.setLocationComponentEnabled(state);
    }

    @Override
    public Location getLastKnownLocation() {
        return locationComponent.getLastKnownLocation();
    }
}
