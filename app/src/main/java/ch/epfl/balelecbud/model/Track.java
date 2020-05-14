package ch.epfl.balelecbud.model;

import java.util.Objects;

public class Track {

    private String title;
    private String artist;
    private String spotifyURI;
    private int playlistRank;

    public Track() {

    }

    public Track(String title, String artist, String spotifyURI, int playlistRank) {
        this.title = title;
        this.artist = artist;
        this.spotifyURI = spotifyURI;
        this.playlistRank = playlistRank;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getSpotifyURI() {
        return spotifyURI;
    }

    public int getPlaylistRank() {
        return playlistRank;
    }


    @Override
    public boolean equals(Object o) {
        return (o instanceof Track) &&
                Objects.equals(getTitle(), ((Track) o).getTitle()) &&
                Objects.equals(getArtist(), ((Track) o).getArtist()) &&
                Objects.equals(getSpotifyURI(), ((Track) o).getSpotifyURI()) &&
                Objects.equals(getPlaylistRank(), ((Track) o).getPlaylistRank());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getArtist(), getSpotifyURI(), getPlaylistRank());
    }
}
