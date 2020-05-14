package ch.epfl.balelecbud.model;

/**
 * Interface for the map
 */
public interface MyMap {

    /**
     * Initialise the map
     *
     * @param appLocationEnabled when true the current location will be displayed on the map
     * @param defaultLocation    the location at which the map will be open
     * @param zoom               the initial zoom level, min value
     */
    void initialiseMap(boolean appLocationEnabled, Location defaultLocation, double zoom);

    /**
     * Build the given marker and display it on the map
     *
     * @param markerBuilder the builder to display on the map
     * @return              the built marker
     */
    MyMarker addMarker(MyMarker.Builder markerBuilder);

}
