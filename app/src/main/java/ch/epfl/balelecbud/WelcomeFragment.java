package ch.epfl.balelecbud;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import ch.epfl.balelecbud.cloudMessaging.TokenUtil;

public class WelcomeFragment extends Fragment {

    private static final String TAG = WelcomeFragment.class.getSimpleName();
    private SpotifyAppRemote spotifyAppRemote;
    private Activity activity = this.getActivity();

    public static WelcomeFragment newInstance() {
        return (new WelcomeFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TokenUtil.storeToken();

        activity.findViewById(R.id.listen_on_spotify_button).setOnClickListener(v -> connect());

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void connect() {
        Log.d(TAG, "Trying to connect Spotify");
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(this.getString(R.string.spotify_client_id))
                        .setRedirectUri(this.getString(R.string.spotify_redirect_uri))
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(activity, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        Log.d(TAG, "Connected Spotify successfully!");
                        WelcomeFragment.this.spotifyAppRemote = spotifyAppRemote;
                        WelcomeFragment.this.spotifyAppRemote.getPlayerApi()
                                .play(activity.getString(R.string.spotify_balelec_playlist));
                    }

                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "Failed to connect Spotify");
                        Toast.makeText(activity, (throwable.getMessage() != null)
                                        ? activity.getString(R.string.login_spotify_message)
                                        : activity.getString(R.string.download_spotify_message),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}