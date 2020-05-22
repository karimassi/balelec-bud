package ch.epfl.balelecbud.model;

import ch.epfl.balelecbud.R;

/**
 * Enum modeling the different marker types
 */
public enum MarkerType {

    FRIEND("Friend", R.drawable.icon_map_friend),
    BAR("Bar", R.drawable.icon_map_bar),
    FOOD("Food", R.drawable.icon_map_food),
    FIRST_AID("First aid", R.drawable.icon_map_first_aid),
    ATM("ATM", R.drawable.icon_map_atm),
    STAGE("Stage", R.drawable.icon_map_stage),
    WC("WC", R.drawable.icon_map_wc),
    NO_TYPE("None", R.drawable.icon_map_poi);

    private String type;
    private int drawableId;

    MarkerType(String type, int drawableId) {
        this.type = type;
        this.drawableId = drawableId;
    }

    @Override
    public String toString() {
        return type;
    }

    public int getDrawableId() {
        return drawableId;
    }

    /**
     * Convert a {@code PointOfInterestType} into its corresponding {@code MarkerType}
     *
     * @param pointOfInterestType the {@code PointOfInterestType} to convert
     * @return                    the converted {@code MarkerType}
     */
    public static MarkerType getMarkerType(PointOfInterestType pointOfInterestType) {
        switch (pointOfInterestType) {
            case BAR:
                return MarkerType.BAR;
            case FOOD:
                return MarkerType.FOOD;
            case ATM:
                return MarkerType.ATM;
            case STAGE:
                return MarkerType.STAGE;
            case FIRST_AID:
                return MarkerType.FIRST_AID;
            case WC:
                return MarkerType.WC;
            default:
                return MarkerType.NO_TYPE;
        }
    }
}