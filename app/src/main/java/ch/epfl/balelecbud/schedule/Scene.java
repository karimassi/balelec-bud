package ch.epfl.balelecbud.schedule;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import ch.epfl.balelecbud.utils.Preconditions;

public class Scene {

    private final List<Concert> concerts;

    public Scene(List<Concert> concerts) {
        Preconditions.checkNonNull(concerts);
        Preconditions.checkArgument(!concerts.isEmpty(), "List of concerts cannot be empty");
        this.concerts = concerts;
    }

    public List<Concert> getConcerts() {
        return Collections.unmodifiableList(concerts);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Scene)) {
            return false;
        }
        Scene other = (Scene) obj;
        return concerts.equals(other.concerts);
    }

    @Override
    public int hashCode() {
        return concerts.hashCode();
    }

    static class Id {

        private final int id;

        public Id(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }
}
