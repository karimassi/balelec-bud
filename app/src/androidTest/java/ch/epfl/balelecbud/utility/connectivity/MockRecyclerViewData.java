package ch.epfl.balelecbud.utility.connectivity;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

import static org.testng.Assert.assertEquals;

public class MockRecyclerViewData extends RecyclerViewData<Integer, MockViewHolder> {

    private int cacheFirstCount = 0;
    private int cacheOnlyCount = 0;
    private int remoteOnlyCount = 0;

    @Override
    public void reload(Database.Source preferredSource) {
        switch (preferredSource){
            case CACHE_ONLY:
                cacheOnlyCount++;
                break;
            case CACHE_FIRST:
                cacheFirstCount++;
                break;
            case REMOTE_ONLY:
                remoteOnlyCount++;
        }
    }

    @Override
    public void bind(int index, MockViewHolder viewHolder) {}

    public void checkCounts(int cacheFirst, int cacheOnly, int remoteOnly){
        assertEquals(cacheFirst, cacheFirstCount);
        assertEquals(cacheOnly, cacheOnlyCount);
        assertEquals(remoteOnly, remoteOnlyCount);
    }

    public void reset(){
        cacheFirstCount = 0;
        cacheOnlyCount = 0;
        remoteOnlyCount = 0;
    }

}
