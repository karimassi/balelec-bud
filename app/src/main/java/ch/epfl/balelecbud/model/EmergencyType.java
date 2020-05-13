package ch.epfl.balelecbud.model;


import androidx.annotation.NonNull;

public enum EmergencyType {
    THEFT("Theft"), FAINT("Feeling faint"), FIRE("Fire"), FIGHT("Fight");

    private String type;

    EmergencyType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return type;
    }
}

