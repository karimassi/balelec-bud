package ch.epfl.balelecbud.utility.database.query;

public class MyGeoClause implements MyClause {

    private final double centreLongitude;
    private final double centreLatitude;
    //in kilometers
    private final double searchRadius;


    public MyGeoClause(double centreLatitude, double centreLongitude, double searchRadius) {
        this.centreLongitude = centreLongitude;
        this.centreLatitude = centreLatitude;
        this.searchRadius = searchRadius;
    }

    public double getCentreLongitude() {
        return centreLongitude;
    }

    public double getCentreLatitude() {
        return centreLatitude;
    }

    public double getSearchRadius() {
        return searchRadius;
    }

    @Override
    public void accept(MyClauseVisitor visitor) {
        visitor.visit(this);
    }
}