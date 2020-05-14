package ch.epfl.balelecbud.model;

import androidx.annotation.Nullable;

public class EmergencyInformation {
    private String instruction;
    private String name;
    private boolean emergencyNumber;

    public EmergencyInformation() {

    }

    public EmergencyInformation(String name, String instruction, boolean emergencyNumber) {
        this.instruction = instruction;
        this.name = name;
        this.emergencyNumber = emergencyNumber;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getName() {
        return name;
    }

    public boolean isEmergencyNumber() {
            return emergencyNumber;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof EmergencyInformation)
                && emergencyNumber == ((EmergencyInformation) obj).emergencyNumber
                && ((EmergencyInformation) obj).getInstruction().equals(instruction)
                && ((EmergencyInformation) obj).getName().equals(getName());
    }

}
