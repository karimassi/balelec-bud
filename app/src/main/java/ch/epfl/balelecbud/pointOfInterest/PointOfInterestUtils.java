package ch.epfl.balelecbud.pointOfInterest;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.util.TaskToCompletableFutureAdapter;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import uk.co.mgbramwell.geofire.android.GeoFire;
import uk.co.mgbramwell.geofire.android.model.Distance;
import uk.co.mgbramwell.geofire.android.model.DistanceUnit;
import uk.co.mgbramwell.geofire.android.model.QueryLocation;

public class PointOfInterestUtils {

    public static CompletableFuture<Integer> getAmountNearPointOfInterest(PointOfInterest poi){
        double poiLongitude = poi.getLocation().getLongitude();
        double poiLatitude = poi.getLocation().getLatitude();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference myCollection = db.collection(DatabaseWrapper.POINT_OF_INTEREST_PATH);
        GeoFire geoFire = new GeoFire(myCollection);

        QueryLocation queryLocation = QueryLocation.fromDegrees(poiLatitude, poiLongitude);
        Distance searchDistance = new Distance(0.002, DistanceUnit.KILOMETERS);

        return new TaskToCompletableFutureAdapter<>(geoFire.query().whereNearTo(queryLocation, searchDistance).build().get()).thenApply(QuerySnapshot::size);
    }
}
