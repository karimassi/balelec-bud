package ch.epfl.balelecbud.map;

import android.location.Location;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import ch.epfl.balelecbud.BalelecbudApplication;

import static ch.epfl.balelecbud.models.Location.DEFAULT_LOCATION;

public class MapboxMapAdapter implements MyMap {

    private static final LatLng BOUND_CORNER_NW = new LatLng(46.52243, 6.56255);
    private static final LatLng BOUND_CORNER_SE = new LatLng(46.51726, 6.57286);
    private static final LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
            .include(BOUND_CORNER_NW)
            .include(BOUND_CORNER_SE)
            .build();

    private MapboxMap mapboxMap;

    public MapboxMapAdapter(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
    }

    @Override
    public void enableUserLocation(boolean appLocationEnabled) {
        LatLng latLng = DEFAULT_LOCATION.toLatLng();
        if (appLocationEnabled) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(BalelecbudApplication.getAppContext(), mapboxMap.getStyle()).build());
            locationComponent.setLocationComponentEnabled(true);

            Location location = mapboxMap.getLocationComponent().getLastKnownLocation();
            latLng = (location != null) ? new LatLng(location) : latLng;
        }
        mapboxMap.setCameraPosition(new CameraPosition.Builder().target(latLng).build());
    }

    @Override
    public MyMarker addMarker(MyMarker.Builder markerBuilder) {
        return (markerBuilder != null) ? new MapboxMarkerAdapter(mapboxMap.addMarker(markerBuilder.toMapboxMarkerOptions())) : null;
    }

    @Override
    public void initialiseMap(boolean appLocationEnabled) {
        mapboxMap.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);
        mapboxMap.setMinZoomPreference(14);
        mapboxMap.setStyle(Style.LIGHT, style -> enableUserLocation(appLocationEnabled));
    }
}
