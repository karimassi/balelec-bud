package ch.epfl.balelecbud.map;

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
}