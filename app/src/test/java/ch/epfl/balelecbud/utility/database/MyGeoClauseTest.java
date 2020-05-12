package ch.epfl.balelecbud.utility.database;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ch.epfl.balelecbud.utility.database.query.MyGeoClause;

@RunWith(JUnit4.class)
public class MyGeoClauseTest {

    private MyGeoClause geoClause = new MyGeoClause(1, 2, 3);

    @Test
    public void getCentreLatitudeTest() {
        Assert.assertEquals(1, geoClause.getCentreLatitude(), 0e-9);
    }

    @Test
    public void getCentreLongitudeTest() {
        Assert.assertEquals(2, geoClause.getCentreLongitude(), 0e-9);
    }

    @Test
    public void getSearchRadiusTest() {
        Assert.assertEquals(3, geoClause.getSearchRadius(), 0e-9);
    }


}
