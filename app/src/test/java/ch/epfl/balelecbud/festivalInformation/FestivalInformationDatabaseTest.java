
package ch.epfl.balelecbud.festivalInformation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FestivalInformationDatabaseTest {

    @Test
    public void testFestivalDatabaseWithoutListening() {
        final List<FestivalInformation> info = new ArrayList<>();
        FestivalInformationAdapterFacade facade = new FestivalInformationAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {}
            @Override
            public void notifyItemModified(int position) {}
            @Override
            public void notifyItemRemoved(int position) {}
        };
        FestivalInformationListener listener = new FestivalInformationListener(facade, info);
        BasicDatabase db = new FestivalInformationDatabase();
        db.registerListener(listener);
        db.unregisterListener();

    }
}