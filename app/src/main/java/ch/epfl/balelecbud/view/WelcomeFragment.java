package ch.epfl.balelecbud.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;

public class WelcomeFragment extends Fragment {

    private static final String TAG = WelcomeFragment.class.getSimpleName();
    private SpotifyAppRemote spotifyAppRemote;

    public static WelcomeFragment newInstance() {
        return (new WelcomeFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getView().findViewById(R.id.spotify_button).setOnClickListener(v -> spotifyConnect());
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
                        WelcomeFragment.this.spotifyAppRemote = spotifyAppRemote;
                        WelcomeFragment.this.spotifyAppRemote.getPlayerApi().play(getContext().getString(R.string.spotify_balelec_playlist));
                        Toast.makeText(BalelecbudApplication.getAppContext(), getContext().getString(R.string.playing_spotify_message), Toast.LENGTH_LONG).show();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "Failed to connect Spotify");
                        Toast.makeText(BalelecbudApplication.getAppContext(), (throwable.getMessage() != null)
                                        ? getContext().getString(R.string.login_spotify_message)
                                        : getContext().getString(R.string.download_spotify_message),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @VisibleForTesting
    public SpotifyAppRemote getSpotifyAppRemote() {
        return spotifyAppRemote;
    }
}