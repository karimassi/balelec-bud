package ch.epfl.balelecbud.emergency.models;

public class EmergencyNumbers {
    private String name;
    private String number;

    public EmergencyNumbers(String name, String number){
        this.name=name;
        this.number =number;
    }

    public String getNumber(){ return number;}
    public String getName(){ return name;}

}
