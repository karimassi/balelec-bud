package ch.epfl.balelecbud.model;

import androidx.annotation.Nullable;

/**
 * Class modeling an emergency information
 */
public final class EmergencyInformation {

    private String instruction;
    private String name;
    private boolean emergencyNumber;

    /**
     * Empty constructor used by FireStore
     */
    public EmergencyInformation() { }

    /**
     * Constructor for emergencies information
     *
     * @param name            the name of the emergency service
     * @param instruction     the instruction or the phone number for the emergency
     * @param emergencyNumber if {@code true} the {@code instruction} contains a phone number to call
     *                        otherwise it contains a text instruction
     */
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
