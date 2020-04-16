package ch.epfl.balelecbud.map;

public interface MyMap {

    void initialiseMap(boolean appLocationEnabled);

    MyMarker addMarker(MyMarker.Builder markerBuilder);


}
