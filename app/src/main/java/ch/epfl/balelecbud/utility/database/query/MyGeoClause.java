package ch.epfl.balelecbud.utility.database.query;

/**
 * Class modeling a geographic claus represented by a disc
 */
public final class MyGeoClause implements MyClause {

    private final double centreLongitude;
    private final double centreLatitude;
    //in kilometers
    private final double searchRadius;

    /**
     * Constructor for geographic claus
     *
     * @param centreLatitude  the latitude of the center
     * @param centreLongitude the longitude of the center
     * @param searchRadius    the radius of the disc
     */
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
