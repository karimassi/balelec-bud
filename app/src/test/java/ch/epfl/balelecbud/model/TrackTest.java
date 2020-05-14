package ch.epfl.balelecbud.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TrackTest {


    private Track track = new Track("Best song", "Best artist", "uri", 1);

    @Test
    public void testEmptyConstructor() {
        new Track();
    }

    @Test
    public void getTitleTest() {
        assertEquals("Best song", track.getTitle());
    }

    @Test
    public void getArtistTest() {
        assertEquals("Best artist", track.getArtist());
    }

    @Test
    public void getUriTest() {
        assertEquals("uri", track.getSpotifyURI());
    }

    @Test
    public void getRankTest() {
        assertEquals(1, track.getPlaylistRank());
    }

    @Test
    public void equalsTest() {
        Track other = new Track("Worst song", "Best artist", "uri", 1);
        assertNotEquals(track, other);

        other = new Track("Best song", "Worst artist", "uri", 1);
        assertNotEquals(track, other);

        other = new Track("Best song", "Best artist", "Uri", 1);
        assertNotEquals(track, other);

        other = new Track("Best song", "Best artist", "uri", 2);
        assertNotEquals(track, other);

        other = new Track("Best song", "Best artist", "uri", 1);
        assertEquals(track, other);
        assertEquals(track.hashCode(), other.hashCode());


        assertNotEquals(track, new Object());
        assertNotEquals(track, null);

    }



}
