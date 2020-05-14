package ch.epfl.balelecbud.model;

import java.util.Objects;

public class Track {

    private String title;
    private String artist;
    private String uri;
    private int rank;

    public Track() {

    }

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
    public boolean equals(Object o) {
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