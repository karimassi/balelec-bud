package ch.epfl.balelecbud.model;

import java.util.Objects;

import javax.annotation.Nullable;

/**
 * Class modeling Spotify's music track
 */
public final class Track {

    private String title;
    private String artist;
    private String uri;
    private int rank;

    /**
     * Empty constructor used by FireStore
     */
    public Track() { }

    /**
     * Constructor for Spotifiy's music track
     *
     * @param title  the title of the music
     * @param artist the artist fo the music
     * @param uri    the Spotify uri of the music
     * @param rank   the rank of the music in the playlist
     */
    public Track(String title, String artist, String uri, int rank) {
        this.title = title;
        this.artist = artist;
        this.uri = uri;
        this.rank = rank;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getUri() {
        return uri;
    }

    public int getRank() {
        return rank;
    }
    @Override
    public boolean equals(@Nullable Object o) {
        return (o instanceof Track) &&
                Objects.equals(getTitle(), ((Track) o).getTitle()) &&
                Objects.equals(getArtist(), ((Track) o).getArtist()) &&
                Objects.equals(getUri(), ((Track) o).getUri()) &&
                Objects.equals(getRank(), ((Track) o).getRank());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getArtist(), getUri(), getRank());
    }
}
