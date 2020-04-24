package ch.epfl.balelecbud.emergency.models;

import androidx.annotation.Nullable;

public class EmergencyInfo {
    private String instruction;
    private String name;

    public EmergencyInfo(){

    }

    public EmergencyInfo(String name,String instruction){
        this.instruction = instruction;
        this.name=name;

    }

    public String getInstruction(){ return instruction;}
    public String getName(){ return name;}

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof EmergencyInfo)
                && ((EmergencyInfo) obj).getInstruction().equals(instruction)
                && ((EmergencyInfo) obj).getName().equals(getName());
    }

}
