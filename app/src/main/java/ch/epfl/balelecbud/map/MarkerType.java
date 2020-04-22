package ch.epfl.balelecbud.map;

import ch.epfl.balelecbud.pointOfInterest.PointOfInterestType;

public enum MarkerType {

    FRIEND("Friend"), BAR("Bar"), FOOD("Food"), FIRST_AID("First aid"), ATM("ATM"), STAGE("Stage"), WC("WC"), NOTYPE("None");

    private String type;

    MarkerType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;


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
            default:
                return MarkerType.NOTYPE;
        }
    }
}