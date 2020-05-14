package ch.epfl.balelecbud.view.playlist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import androidx.annotation.VisibleForTesting;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import ch.epfl.balelecbud.model.Track;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.database.Database;
import ch.epfl.balelecbud.utility.database.query.MyQuery;
import ch.epfl.balelecbud.utility.recyclerViews.OnRecyclerViewInteractionListener;
import ch.epfl.balelecbud.utility.recyclerViews.RecyclerViewData;

import static ch.epfl.balelecbud.BalelecbudApplication.getAppDatabase;
import static ch.epfl.balelecbud.BalelecbudApplication.getAppStorage;

public class TrackData extends RecyclerViewData<Track, TrackHolder> {

    private static Consumer<Intent> intentLauncher;
    private OnRecyclerViewInteractionListener<Track> interactionListener;

    public TrackData(Activity activity, OnRecyclerViewInteractionListener<Track> interactionListener) {
        super();
        this.interactionListener = interactionListener;
        if (intentLauncher == null) {
            intentLauncher = activity::startService;
        }
    }

    @Override
    public void reload(Database.Source preferredSource) {
        MyQuery query = new MyQuery(Database.PLAYLIST_PATH, new LinkedList<>(), preferredSource);
        getAppDatabase().query(query, Track.class)
                .thenApply(playlistTracks -> {
                    Collections.sort(playlistTracks, (track1, track2) ->
                            Integer.compare(track1.getRank(), track2.getRank()));
                    return playlistTracks;
                })
              .whenComplete(new CompletableFutureUtils.MergeBiConsumer<>(this));
    }

    @Override
    public void bind(int index, TrackHolder viewHolder) {
        viewHolder.title.setText(data.get(index).getTitle());
        viewHolder.artist.setText(data.get(index).getArtist());
        Log.d("TrackData", "tracks_images/" + data.get(index).getUri() + ".jpeg");
        CompletableFuture<File> imageDownload = getAppStorage()
                .getFile("tracks_images/" + data.get(index).getUri() + ".jpeg");
        imageDownload.whenComplete((file, t) -> {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            viewHolder.image.setImageBitmap(bitmap);
            viewHolder.image.setVisibility(View.VISIBLE);
        });

        if(interactionListener != null) {
            viewHolder.itemView.setOnClickListener(v -> interactionListener.onItemSelected(data.get(index)));
        }
    }

    @VisibleForTesting
    public static void setIntentLauncher(Consumer<Intent> launcher) {
        intentLauncher = launcher;
    }
}
