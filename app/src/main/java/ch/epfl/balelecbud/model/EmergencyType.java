package ch.epfl.balelecbud.model;


import androidx.annotation.NonNull;

public enum EmergencyType {
    THEFT {
        @NonNull
        @Override
        public String toString() {
            return "Theft";
        }
    }, FAINTNESS {
        @NonNull
        @Override
        public String toString() {
            return "Faintness";
        }
    };
}
