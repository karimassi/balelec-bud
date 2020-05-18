package ch.epfl.balelecbud.model;

import androidx.annotation.NonNull;

/**
 * Enum modeling the emergency categories
 */
public enum EmergencyCategory {
    THEFT("Theft"),
    FAINT("Feeling faint"),
    FIRE("Fire"),
    FIGHT("Fight");

    private String type;

    EmergencyCategory(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return type;
    }
}

