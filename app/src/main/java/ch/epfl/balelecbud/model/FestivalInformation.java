package ch.epfl.balelecbud.model;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Class modeling general festival information
 */
public final class FestivalInformation {

    private String title;
    private String information;

    /**
     * Empty constructor used by FireStore
     */
    public FestivalInformation() { }

    /**
     * Constructor for the festival information
     *
     * @param title       the title of the festival information
     * @param information the content of the festival information
     */
    public FestivalInformation(String title, String information) {
        this.title = title;
        this.information = information;
    }

    public String getTitle() {
        return title;
    }

    public String getInformation() {
        return information;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof FestivalInformation)
                && Objects.equals(((FestivalInformation) obj).information, information)
                && Objects.equals(((FestivalInformation) obj).title, title);
    }
}
