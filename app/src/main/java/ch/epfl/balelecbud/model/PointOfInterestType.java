package ch.epfl.balelecbud.model;

import androidx.annotation.NonNull;

import ch.epfl.balelecbud.R;

/**
 * Enum modeling the different types of points of interest of the festival
 */
public enum PointOfInterestType {
    BAR("Bar", R.drawable.icon_map_bar),
    FOOD("Food", R.drawable.icon_food),
    FIRST_AID("First aid", R.drawable.icon_first_aid),
    ATM("ATM", R.drawable.icon_atm),
    WC("WC", R.drawable.icon_wc),
    STAGE("Stage", R.drawable.icon_stage);

    private String type;
    private int drawableId;

    PointOfInterestType(String type, int drawableId) {
        this.type = type;
        this.drawableId = drawableId;
    }

    @NonNull
    @Override
    public String toString() {
        return type;
    }

    public int getDrawableId() {
        return drawableId;
    }
}