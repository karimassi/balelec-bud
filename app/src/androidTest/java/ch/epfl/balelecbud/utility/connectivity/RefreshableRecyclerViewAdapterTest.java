package ch.epfl.balelecbud.utility.connectivity;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;

@RunWith(AndroidJUnit4.class)
public class RefreshableRecyclerViewAdapterTest {

    private MockConnectivityChecker mockConnectivityChecker;

    @Before
    public void setup() {
        mockConnectivityChecker = new MockConnectivityChecker();
        BalelecbudApplication.setConnectivityChecker(mockConnectivityChecker);
    }

    @Test
    public void reloadDataSourceIsCacheOnlyWhenOffline(){
        mockConnectivityChecker.setAvailable(false);
        MockRecyclerViewData mockRecyclerViewData = new MockRecyclerViewData();
        RefreshableRecyclerViewAdapter<Integer, MockViewHolder> dut =
                new RefreshableRecyclerViewAdapter<>(null, null, mockRecyclerViewData, 0);
        mockRecyclerViewData.reset();
        dut.reloadData();
        mockRecyclerViewData.checkCounts(0,1,0);
    }

    @Test
    public void reloadDataSourceIsRemoteOnlyWhenOnline(){
        mockConnectivityChecker.setAvailable(true);
        MockRecyclerViewData mockRecyclerViewData = new MockRecyclerViewData();
        RefreshableRecyclerViewAdapter<Integer, MockViewHolder> dut =
                new RefreshableRecyclerViewAdapter<>(null, null, mockRecyclerViewData, 0);
        mockRecyclerViewData.reset();
        dut.reloadData();
        mockRecyclerViewData.checkCounts(0,0,1);
    }

    @Test
    public void constructorReloadSourceIsCacheOnlyWhenOffline(){
        mockConnectivityChecker.setAvailable(false);
        MockRecyclerViewData mockRecyclerViewData = new MockRecyclerViewData();
        RefreshableRecyclerViewAdapter<Integer, MockViewHolder> dut =
                new RefreshableRecyclerViewAdapter<>(null, null, mockRecyclerViewData, 0);
        mockRecyclerViewData.checkCounts(0,1,0);
    }

    @Test
    public void constructorReloadSourceIsCacheFirstWhenOnline(){
        mockConnectivityChecker.setAvailable(true);
        MockRecyclerViewData mockRecyclerViewData = new MockRecyclerViewData();
        RefreshableRecyclerViewAdapter<Integer, MockViewHolder> dut =
                new RefreshableRecyclerViewAdapter<>(null, null, mockRecyclerViewData, 0);
        mockRecyclerViewData.checkCounts(1,0,0);
    }

}
