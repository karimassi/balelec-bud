package ch.epfl.balelecbud.FestivalInformation;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FestivalInformationProviderTest {

    private List<FestivalInformation> DUMMY_INFORMATION = new ArrayList<FestivalInformation>(Arrays.asList(
            new FestivalInformation("boissons alcoolisées", "vente interdite de boissons alcoolisées sur le site du festival"),
            new FestivalInformation("boissons alcoolisées", "vente interdite de boissons non-alcoolisées sur le site du festival"),
            new FestivalInformation("parapluies", "parapluies interdits")));

    private FestivalInformationProvider provider;
    private BasicDatabase mockDB;


    @Before
    public void init() {
        provider = new FestivalInformationProvider();
        mockDB = new BasicDatabase() {
            @Override
            public void registerListener(FestivalInformationListener listener) {}
            @Override
            public void unregisterListener() {}
            @Override
            public void listen() {}
        };
    }

    @After
    public void end() {
        provider.unsubscribe();
    }


    @Test
    public void testItemAddedNotifiesAndUpdatesList() {
        final List<FestivalInformation> info = new ArrayList<>();
        FestivalInformationAdapterFacade facade = new FestivalInformationAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                Assert.assertEquals(DUMMY_INFORMATION.get(0), info.get(position));
            }
            @Override
            public void notifyItemModified(int position) {}
            @Override
            public void notifyItemRemoved(int position) {}
        };
        FestivalInformationListener listener = new FestivalInformationListener(facade, info);
        provider.subscribe(listener, mockDB);
        listener.addInformation(DUMMY_INFORMATION.get(0));
    }

    @Test
    public void testItemAddedThenRemovedNotifiesAndUpdatesList() {
        final List<FestivalInformation> info = new ArrayList<>();
        FestivalInformationAdapterFacade facade = new FestivalInformationAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                Assert.assertEquals(DUMMY_INFORMATION.get(0), info.get(position));
            }
            @Override
            public void notifyItemModified(int position) {}
            @Override
            public void notifyItemRemoved(int position) {
                Assert.assertEquals(0, info.size());
            }
        };
        FestivalInformationListener listener = new FestivalInformationListener(facade, info);
        provider.subscribe(listener, mockDB);

        listener.addInformation(DUMMY_INFORMATION.get(0));
        listener.removeInformation(DUMMY_INFORMATION.get(0), 0);
    }

    @Test
    public void testItemAddedThenModifiedNotifiesAndUpdatesList() {
        final List<FestivalInformation> info = new ArrayList<>();
        FestivalInformationAdapterFacade facade = new FestivalInformationAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {
                Assert.assertEquals(DUMMY_INFORMATION.get(0), info.get(position));
            }
            @Override
            public void notifyItemModified(int position) {
                Assert.assertEquals(DUMMY_INFORMATION.get(1), info.get(position));
            }
            @Override
            public void notifyItemRemoved(int position) {
            }
        };
        FestivalInformationListener listener = new FestivalInformationListener(facade, info);
        provider.subscribe(listener, mockDB);

        listener.addInformation(DUMMY_INFORMATION.get(0));
        listener.modifyInformation(DUMMY_INFORMATION.get(1), 0);
    }

    @Test
    public void testNonexistantItemRemoved() {
        final List<FestivalInformation> info = new ArrayList<>();
        FestivalInformationAdapterFacade facade = new FestivalInformationAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {}
            @Override
            public void notifyItemModified(int position) {}
            @Override
            public void notifyItemRemoved(int position) {
                Assert.assertEquals(DUMMY_INFORMATION.subList(0,1), info);
            }
        };
        FestivalInformationListener listener = new FestivalInformationListener(facade, info);
        provider.subscribe(listener, mockDB);

        listener.addInformation(DUMMY_INFORMATION.get(1));
        listener.removeInformation(DUMMY_INFORMATION.get(0), 0);
    }

    @Test
    public void testCorrectItemRemoved() {
        final List<FestivalInformation> info = new ArrayList<>();
        FestivalInformationAdapterFacade facade = new FestivalInformationAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {}
            @Override
            public void notifyItemModified(int position) {}
            @Override
            public void notifyItemRemoved(int position) {
                Assert.assertEquals(DUMMY_INFORMATION.subList(1,2), info);
            }
        };
        FestivalInformationListener listener = new FestivalInformationListener(facade, info);
        provider.subscribe(listener, mockDB);

        listener.addInformation(DUMMY_INFORMATION.get(0));
        listener.addInformation(DUMMY_INFORMATION.get(1));
        listener.removeInformation(DUMMY_INFORMATION.get(0), 0);
    }

    @Test
    public void testItemNotModifiedIfOutOfBoundsIndex() {
        final List<FestivalInformation> info = new ArrayList<>();
        FestivalInformationAdapterFacade facade = new FestivalInformationAdapterFacade() {
            @Override
            public void notifyItemInserted(int position) {}
            @Override
            public void notifyItemModified(int position) {
                Assert.assertEquals(DUMMY_INFORMATION.subList(0,2), info);
            }
            @Override
            public void notifyItemRemoved(int position) {
            }
        };
        FestivalInformationListener listener = new FestivalInformationListener(facade, info);
        provider.subscribe(listener, mockDB);

        listener.addInformation(DUMMY_INFORMATION.get(0));
        listener.addInformation(DUMMY_INFORMATION.get(1));
        listener.modifyInformation(DUMMY_INFORMATION.get(2), 4);
    }
}
