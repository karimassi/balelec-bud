package ch.epfl.balelecbud.spotify;

import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

public class SpotifyFragment extends Fragment {

    private static final String TAG = SpotifyFragment.class.getSimpleName();
    private static final String CLIENT_ID = "7a2fb16c68b44c5b9fc7397f6d25f714";
    private static final String REDIRECT_URI = "ch.epfl.balelecbud://callback";

    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "Started connection");
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this.getContext(), connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d(TAG, "Connected successfully!");
                        connected();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "Failure connecting with spotify app remote");
                        Toast.makeText(SpotifyFragment.this.getContext(), "Download Spotify!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Stopped connection");
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX7K31D69s4M1");

        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        displayTrack(track);
                    }
                });
    }

    private void displayTrack(Track track) {
        Log.d(TAG, track.name + " by " + track.artist.name);

    }

    /* Welcome: attributes
    private static final String SPOTIFY_CLIENT_ID = "7a2fb16c68b44c5b9fc7397f6d25f714";
    private static final String REDIRECT_URI = "ch.epfl.balelecbud://callback";
    private static final String BALELEC_PLAYLIST = "spotify:playlist:48yI8Gqy44311pEJggXyp3";
    private static final String DOWNLOAD_MESSAGE = "Download Spotify and log-in to listen to the Balelec playlist";
    private static final String LOGIN_MESSAGE = "Go to Spotify and log-in to listen to the Balelec playlist";

    private SpotifyAppRemote spotifyAppRemote;
    private Context activity = this;
     */

    /* Welcome

        findViewById(R.id.listen_on_spotify_button).setOnClickListener(v -> connect());
    }

    private void connect() {
        Log.d(TAG, "Trying to connect Spotify");
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(SPOTIFY_CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        Log.d(TAG, "Connected Spotify successfully!");
                        WelcomeActivity.this.spotifyAppRemote = spotifyAppRemote;
                        WelcomeActivity.this.spotifyAppRemote.getPlayerApi().play(BALELEC_PLAYLIST);
                    }

                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "Failed to connect Spotify");
                        Toast.makeText(activity, (throwable.getMessage() != null) ?
                                LOGIN_MESSAGE : DOWNLOAD_MESSAGE, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void disconnect() {
        Log.d(TAG, "Stopped connection");
        if(spotifyAppRemote != null) SpotifyAppRemote.disconnect(spotifyAppRemote);
    }
     */

    /* activity welcome after frame layout
    <Button
                android:id="@+id/listen_on_spotify_button"
                android:layout_width="188.96dp"
                android:layout_height="56.64dp"
                android:layout_gravity="center|bottom"
                android:layout_marginBottom="66dp"
                android:background="@drawable/spotify_logo_white" />
     */
}
