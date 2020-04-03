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

    public static CompletableFuture<List<TransportStation>> getNearbyStations(Location currentLocation) {
        String parameter_x = "x=" + currentLocation.getLatitude();
        String parameter_y = "y=" + currentLocation.getLongitude();
        String url = "http://transport.opendata.ch/v1/locations?"+parameter_x+"&"+parameter_y;
        return BalelecbudApplication.getHttpClient().get(url)
                .thenApply(jsonElement -> {
                    List<TransportStation> stations = new ArrayList<>();
                    JsonArray stationsJson = jsonElement.getAsJsonObject().get("stations").getAsJsonArray();
                    for (int i = 0; i < stationsJson.size(); ++i) {
                        JsonObject stationObject = stationsJson.get(i).getAsJsonObject();
                        if (!stationObject.get("id").isJsonNull()) {
                            JsonObject locationObject = stationObject.get("coordinate").getAsJsonObject();
                            Location location = new Location(locationObject.get("x").getAsDouble(), locationObject.get("y").getAsDouble());
                            stations.add(new TransportStation(location,
                                    stationObject.get("id").getAsString(),
                                    stationObject.get("name").getAsString(),
                                    stationObject.get("distance").getAsDouble()));
                        }
                    }
                    return stations;
                });
    }


    public static CompletableFuture<List<TransportDeparture>> getNextDepartures(TransportStation station) {
        String parameter_id = "id=" + station.getStationId();
        String url = "http://transport.opendata.ch/v1/stationboard?"+parameter_id+"&limit=20";
        return BalelecbudApplication.getHttpClient().get(url)
                .thenApply(jsonElement -> {
                    List<TransportDeparture> departures = new ArrayList<>();
                    JsonObject stationJson = jsonElement.getAsJsonObject().get("station").getAsJsonObject();
                    if (!stationJson.get("id").getAsString().equals(station.getStationId())) {
                        return departures;
                    }
                    JsonArray stationboard = jsonElement.getAsJsonObject().get("stationboard").getAsJsonArray();
                    for (int i = 0; i < stationboard.size(); ++i) {
                        JsonObject departureObject = stationboard.get(i).getAsJsonObject();
                        Long timestamp = departureObject.get("stop").getAsJsonObject().get("departureTimestamp").getAsLong();
                        departures.add(new TransportDeparture(departureObject.get("category").getAsString(),
                                departureObject.get("number").getAsString(),
                                departureObject.get("to").getAsString(),
                                new Date(timestamp*1000)));
                    }
                    return departures;
                });

    }

}
