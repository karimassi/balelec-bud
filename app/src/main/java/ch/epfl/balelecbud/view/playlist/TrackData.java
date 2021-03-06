package ch.epfl.balelecbud.view.playlist;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.model.Track;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.InformationSource;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.recyclerViews.OnRecyclerViewInteractionListener;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.utility.storage.Storage.TRACKS_IMAGES;

public final class TrackData extends RecyclerViewData<Track, TrackHolder> {

    private static String TAG = TrackData.class.getSimpleName();

    private OnRecyclerViewInteractionListener<Track> interactionListener;

    TrackData(OnRecyclerViewInteractionListener<Track> interactionListener) {
        super();
        this.interactionListener = interactionListener;
    }

    @Override
    public CompletableFuture<Long> reload(InformationSource preferredSource) {
        MyQuery query = new MyQuery(Database.PLAYLIST_PATH, new LinkedList<>(), preferredSource);
        return getAppDatabase().query(query, Track.class)
                .thenApply(playlistTracks -> {
                    Collections.sort(playlistTracks.getList(), (track1, track2) ->
                            Integer.compare(track1.getRank(), track2.getRank()));
                    return playlistTracks;
                })
              .thenApply(new CompletableFutureUtils.MergeFunction<>(this));
    }

    @Override
    public void bind(int index, TrackHolder viewHolder) {
        viewHolder.title.setText(data.get(index).getTitle());
        viewHolder.artist.setText(data.get(index).getArtist());

        CompletableFutureUtils.downloadAndSetImageView(TRACKS_IMAGES + "/" + data.get(index).getUri() + ".jpeg",
                viewHolder.image, TAG, "Couldn't load track image");

        viewHolder.itemView.setOnClickListener(v -> interactionListener.onItemSelected(data.get(index)));
    }
}
