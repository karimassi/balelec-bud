package ch.epfl.balelecbud;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

public class SpotifyActivity extends BasicActivity {

    private static final String CLIENT_ID = "7a2fb16c68b44c5b9fc7397f6d25f714";
    private static final String REDIRECT_URI = "ch.epfl.balelecbud://callback";
    private static final String TAG = SpotifyActivity.class.getSimpleName();
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Started connection");
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d(TAG, "Connected successfully!");
                        connected();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, "Failure connecting with spotify app remote...");
                        Log.e(TAG, throwable.getMessage(), throwable);
                    }
                });
    }

    @Override
    protected void onStop() {
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
        ImageView albumImageView = new ImageView(this);

        mSpotifyAppRemote.getImagesApi().getImage(track.imageUri)
                .setResultCallback(albumImageView::setImageBitmap);
    }
}
