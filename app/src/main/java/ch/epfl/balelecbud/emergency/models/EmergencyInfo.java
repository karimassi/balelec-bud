package ch.epfl.balelecbud.emergency.models;

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

}
