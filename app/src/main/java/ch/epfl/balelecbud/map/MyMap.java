package ch.epfl.balelecbud.map;

public interface MyMap {
    void setMyLocationEnabled(boolean locationEnabled);

    MyMarker addMarker(MyMarker.Builder markerBuilder);
}
