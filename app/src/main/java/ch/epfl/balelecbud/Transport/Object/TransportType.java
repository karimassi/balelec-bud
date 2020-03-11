package ch.epfl.balelecbud.Transport.Object;

import androidx.annotation.NonNull;

enum TransportType {
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
