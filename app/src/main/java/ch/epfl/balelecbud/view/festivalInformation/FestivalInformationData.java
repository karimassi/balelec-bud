package ch.epfl.balelecbud.view.festivalInformation;

import java.util.LinkedList;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.model.FestivalInformation;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

public class FestivalInformationData extends RecyclerViewData<FestivalInformation, FestivalInformationHolder> {

    @Override
    public void reload(Database.Source preferredSource) {
        MyQuery query = new MyQuery(Database.FESTIVAL_INFORMATION_PATH, new LinkedList<>(), preferredSource);
        BalelecbudApplication.getAppDatabase().query(query, FestivalInformation.class)
            .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
    }

    @Override
    public void bind(int index, FestivalInformationHolder viewHolder) {
        viewHolder.informationTitleTextView.setText(data.get(index).getTitle());
        viewHolder.informationContentTextView.setText(data.get(index).getInformation());
    }
}
