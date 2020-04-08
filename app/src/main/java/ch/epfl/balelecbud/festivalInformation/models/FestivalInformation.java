package ch.epfl.balelecbud.festivalInformation.models;

import androidx.annotation.Nullable;

public class FestivalInformation {

    private String title;
    private String information;

    public FestivalInformation() {

    }

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
