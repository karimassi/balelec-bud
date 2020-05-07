package ch.epfl.balelecbud.spotify;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.RootActivity;

public class SpotifyActivity extends AppCompatActivity {

    private static final String TAG = SpotifyActivity.class.getSimpleName();

    private SpotifyAppRemote spotifyAppRemote;
    private Activity activity = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_nav_header);

        activity.findViewById(R.id.listen_on_spotify_button).setOnClickListener(v -> spotifyConnect());
    }

    private void spotifyConnect() {
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
                        SpotifyActivity.this.spotifyAppRemote = spotifyAppRemote;
                        SpotifyActivity.this.spotifyAppRemote.getPlayerApi()
                                .play(activity.getString(R.string.spotify_balelec_playlist));
                    }

                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "Failed to connect Spotify");
                        Toast.makeText(BalelecbudApplication.getAppContext(), (throwable.getMessage() != null)
                                        ? activity.getString(R.string.login_spotify_message)
                                        : activity.getString(R.string.download_spotify_message),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Stopped connection");
        SpotifyAppRemote.disconnect(spotifyAppRemote);
    }
}
