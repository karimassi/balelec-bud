package ch.epfl.balelecbud.model;

import androidx.annotation.Nullable;

import com.google.rpc.Help;

import java.util.Objects;

/**
 * Class modeling a help page displayed in the welcome activity
 */
public final class HelpPage {

    private String title;
    private String description;

    public HelpPage(){

    }

    public HelpPage(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof HelpPage) &&
                Objects.equals(((HelpPage) obj).title, title) &&
                Objects.equals(((HelpPage) obj).description, description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }
}
