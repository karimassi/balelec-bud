package ch.epfl.balelecbud.model;

import androidx.annotation.Nullable;

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
                && ((FestivalInformation) obj).getInformation().equals(information)
                && ((FestivalInformation) obj).getTitle().equals(title);
    }
}
