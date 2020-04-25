package ch.epfl.balelecbud.emergency.models;

import androidx.annotation.Nullable;

public class EmergencyNumber {
    private String name;
    private String number;

    public EmergencyNumber(){

    }

    public EmergencyNumber(String name, String number){
        this.name=name;
        this.number =number;
    }

    public String getNumber(){ return number;}
    public String getName(){ return name;}


    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof EmergencyNumber)
                && ((EmergencyNumber) obj).getName().equals(name)
                && ((EmergencyNumber) obj).getNumber().equals(number);
    }

}
