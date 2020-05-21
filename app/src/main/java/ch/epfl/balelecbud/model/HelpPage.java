package ch.epfl.balelecbud.model;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Class modeling a help page displayed in the welcome fragment
 */
public final class HelpPage {

    private String imageName;
    private String title;
    private String description;

    public HelpPage() {}

    public HelpPage(String imageName, String title, String description) {
        this.imageName = imageName;
        this.title = title;
        this.description = description;
    }

    public String getImageName() {
        return imageName;
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
                Objects.equals(((HelpPage) obj).imageName, imageName) &&
                Objects.equals(((HelpPage) obj).title, title) &&
                Objects.equals(((HelpPage) obj).description, description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageName, title, description);
    }
}