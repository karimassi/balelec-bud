package ch.epfl.balelecbud.transport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.models.Location;
import ch.epfl.balelecbud.transport.objects.TransportDeparture;
import ch.epfl.balelecbud.transport.objects.TransportStation;

public class TransportUtil {

    private static final String BASE_URL = "http://transport.opendata.ch/v1/";
    private static final String LOCATIONS_PATH = "locations";
    private static final String STATIONBOARD_PATH = "stationboard";

    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String COORDINATE_KEY = "coordinate";
    private static final String DISTANCE_TO_USER_KEY = "distance";
    private static final String STATION_KEY = "station";
    private static final String STATIONS_LIST_KEY = "stations";
    private static final String STATIONBOARD_KEY = "stationboard";
    private static final String LINE_TYPE_KEY = "category";
    private static final String LINE_NUMBER_KEY = "number";
    private static final String LINE_TERMINUS_KEY = "to";
    private static final String STOP_STATION_KEY = "stop";
    private static final String DEPARTURE_TIMESTAMP_KEY = "departureTimestamp";



    public static CompletableFuture<List<TransportStation>> getNearbyStations(Location currentLocation) {
        String parameter_x = "x=" + currentLocation.getLatitude();
        String parameter_y = "y=" + currentLocation.getLongitude();
        String url = BASE_URL + LOCATIONS_PATH + "?" + parameter_x + "&" + parameter_y;
        return BalelecbudApplication.getHttpClient().get(url)
                .thenApply(jsonElement -> {
                    List<TransportStation> stations = new ArrayList<>();
                    JsonArray stationsJson = jsonElement.getAsJsonObject().get(STATIONS_LIST_KEY).getAsJsonArray();
                    for (int i = 0; i < stationsJson.size(); ++i) {
                        JsonObject stationObject = stationsJson.get(i).getAsJsonObject();
                        if (!stationObject.get(ID_KEY).isJsonNull()) {
                            JsonObject locationObject = stationObject.get(COORDINATE_KEY).getAsJsonObject();
                            Location location = new Location(locationObject.get("x").getAsDouble(), locationObject.get("y").getAsDouble());
                            stations.add(new TransportStation(location,
                                    stationObject.get(ID_KEY).getAsString(),
                                    stationObject.get(NAME_KEY).getAsString(),
                                    stationObject.get(DISTANCE_TO_USER_KEY).getAsDouble()));
                        }
                    }
                    return stations;
                });
    }


    public static CompletableFuture<List<TransportDeparture>> getNextDepartures(TransportStation station) {
        String parameter_id = "id=" + station.getStationId();
        String url = BASE_URL + STATIONBOARD_PATH + "?" + parameter_id + "&limit=20";
        return BalelecbudApplication.getHttpClient().get(url)
                .thenApply(jsonElement -> {
                    List<TransportDeparture> departures = new ArrayList<>();
                    JsonObject stationJson = jsonElement.getAsJsonObject().get(STATION_KEY).getAsJsonObject();
                    if (!stationJson.get(ID_KEY).getAsString().equals(station.getStationId())) {
                        return departures;
                    }
                    JsonArray stationboard = jsonElement.getAsJsonObject().get(STATIONBOARD_KEY).getAsJsonArray();
                    for (int i = 0; i < stationboard.size(); ++i) {
                        JsonObject departureObject = stationboard.get(i).getAsJsonObject();
                        Long timestamp = departureObject.get(STOP_STATION_KEY).getAsJsonObject().get(DEPARTURE_TIMESTAMP_KEY).getAsLong();
                        departures.add(new TransportDeparture(departureObject.get(LINE_TYPE_KEY).getAsString(),
                                departureObject.get(LINE_NUMBER_KEY).getAsString(),
                                departureObject.get(LINE_TERMINUS_KEY).getAsString(),
                                new Date(timestamp*1000)));
                    }
                    return departures;
                });

    }

}
