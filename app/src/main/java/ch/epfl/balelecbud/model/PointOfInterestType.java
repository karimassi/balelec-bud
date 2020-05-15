package ch.epfl.balelecbud.model;

import org.jetbrains.annotations.NotNull;

/**
 * Enum modeling the different type of point of interest of the festival
 */
public enum PointOfInterestType {
    BAR("Bar"),
    FOOD("Food"),
    FIRST_AID("First aid"),
    ATM("ATM"),
    WC("WC"),
    STAGE("Stage");

    private String type;

    PointOfInterestType(String type) {
        this.type = type;
    }

    @NotNull
    @Override
    public String toString() {
        return type;
    }
}