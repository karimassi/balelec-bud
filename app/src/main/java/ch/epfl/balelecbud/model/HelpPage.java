package ch.epfl.balelecbud.model;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Class modeling a help page displayed in the welcome fragment
 */
public class HelpPage {

    private String image;
    private String title;
    private String description;

    public HelpPage(String image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof HelpPage) &&
                Objects.equals(((HelpPage) obj).image, image) &&
                Objects.equals(((HelpPage) obj).title, title) &&
                Objects.equals(((HelpPage) obj).description, description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, title, description);
    }
}