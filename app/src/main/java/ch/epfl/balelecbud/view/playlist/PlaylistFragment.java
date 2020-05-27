package ch.epfl.balelecbud.view.playlist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.Track;
import ch.epfl.balelecbud.utility.recyclerViews.OnRecyclerViewInteractionListener;
import ch.epfl.balelecbud.utility.recyclerViews.RefreshableRecyclerViewAdapter;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

public final class PlaylistFragment extends Fragment implements OnRecyclerViewInteractionListener<Track> {

    private static final String TAG = PlaylistFragment.class.getSimpleName();
    private SpotifyAppRemote spotifyAppRemote;

    public static PlaylistFragment newInstance() {
        return (new PlaylistFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotifyConnect();
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view_playlist);
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout_playlist);

        TrackData data = new TrackData(this);
        RefreshableRecyclerViewAdapter<Track, TrackHolder> adapter = new RefreshableRecyclerViewAdapter<>(
                TrackHolder::new, swipeRefreshLayout, data, R.layout.item_track);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.setOnRefreshListener(swipeRefreshLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
        spotifyConnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isSpotifyConnected()) {
            SpotifyAppRemote.disconnect(spotifyAppRemote);
            Log.d(TAG, "Disconnecting the Spotify App Remote");
        }
    }

    @Override
    public void onItemSelected(Track item) {
        String spotifyURI = "spotify:track:" + item.getUri();
        playTrack(spotifyURI);
    }


    private void spotifyConnect() {
        Log.d(TAG, "Trying to connect Spotify");
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(getContext().getString(R.string.spotify_client_id))
                        .setRedirectUri(getContext().getString(R.string.spotify_redirect_uri))
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(getContext(), connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        Log.d(TAG, "Connected Spotify successfully!");
                        PlaylistFragment.this.spotifyAppRemote = spotifyAppRemote;
                    }

                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "Failed to connect Spotify", throwable);
                        Snackbar.make(getView(), (throwable.getMessage() != null)
                                ? getContext().getString(R.string.login_spotify_message)
                                : getContext().getString(R.string.download_spotify_message), LENGTH_LONG)
                                .show();
                    }
                });
    }

    private void playTrack(String trackUri) {
        if(!isSpotifyConnected()) {
            spotifyConnect();
        } else {
            spotifyAppRemote.getPlayerApi().play(trackUri);
            Log.d(TAG, "Playing track with uri: " + trackUri);
        }
    }

    @VisibleForTesting
    public boolean isSpotifyConnected() {
        return spotifyAppRemote != null;
    }
}
