package ch.epfl.balelecbud.pointOfInterest;

public enum PointOfInterestType {
    BAR("Bar"), FOOD("Food"), FIRST_AID("First aid"), ATM("ATM"), WC("WC"), STAGE("Stage");

    private String type;
    PointOfInterestType(String type) {
        this.type = type;
    }

    @Override
    public String toString(){
        return type;
    }

}