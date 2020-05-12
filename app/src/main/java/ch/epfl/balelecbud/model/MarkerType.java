package ch.epfl.balelecbud.model;

import ch.epfl.balelecbud.R;

public enum MarkerType {

    FRIEND("Friend", R.drawable.friend_icon),
    BAR("Bar", R.drawable.poi_bar_icon),
    FOOD("Food", R.drawable.poi_food_icon),
    FIRST_AID("First aid", R.drawable.poi_firstaid),
    ATM("ATM", R.drawable.poi_atm),
    STAGE("Stage", R.drawable.poi_stage),
    WC("WC", R.drawable.poi_wc),
    NO_TYPE("None", R.drawable.map);

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