package ch.epfl.balelecbud.festivalInformation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import ch.epfl.balelecbud.festivalInformation.models.FestivalInformation;
import ch.epfl.balelecbud.util.CompletableFutureUtils;
import ch.epfl.balelecbud.util.database.DatabaseWrapper;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;
import ch.epfl.balelecbud.util.database.MyQuery;
import ch.epfl.balelecbud.util.views.RecyclerViewData;

public class FestivalInformationData extends RecyclerViewData<FestivalInformation, FestivalInformationHolder> {

    private static DatabaseWrapper databaseImplementation = FirestoreDatabaseWrapper.getInstance();

    public static void setDatabaseImplementation(DatabaseWrapper db){
        databaseImplementation = db;
    }

    @Override
    public void reload() {
        MyQuery query = new MyQuery(DatabaseWrapper.FESTIVAL_INFORMATION_PATH, new LinkedList<MyQuery.WhereClause>());
        databaseImplementation.query(query, FestivalInformation.class)
            .whenComplete(new CompletableFutureUtils.MergeBiConsumer<FestivalInformation>(this));
    }

    @Override
    public void bind(int index, FestivalInformationHolder viewHolder) {
        viewHolder.informationTitleTextView.setText(data.get(index).getTitle());
        viewHolder.informationContentTextView.setText(data.get(index).getInformation());
    }
}
