package ch.epfl.balelecbud.transport.objects;

import androidx.annotation.NonNull;

public enum TransportType {
    METRO {
        @NonNull
        @Override
        public String toString() {
            return "Metro";
        }
    },BUS{
        @NonNull
        @Override
        public String toString() {
            return "Bus";
        }
    };
}
