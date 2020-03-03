package ch.epfl.sdp.schedule;

import androidx.annotation.Nullable;

import java.util.Objects;

import ch.epfl.sdp.utils.Preconditions;

public class TimeSlot {

    private final Time startingTime;
    private final Time endingTime;

    //once comparison is added, check that startingTime < endingTime
    public TimeSlot(Time startingTime, Time endingTime) {
        Preconditions.checkNonNull(startingTime);
        Preconditions.checkNonNull(endingTime);
        this.startingTime = startingTime;
        this.endingTime = endingTime;
    }

    public Time getStartingTime() {
        return startingTime;
    }

    public Time getEndingTime() {
        return endingTime;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof TimeSlot)){
            return false;
        }
        TimeSlot other = (TimeSlot) obj;
        return startingTime.equals(other.startingTime) && endingTime.equals(other.endingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingTime, endingTime);
    }

    static class Time {

        //should add comparison at some point

        private final int hour;
        private final int minute;

        public Time(int hour, int minute) {
            Preconditions.checkArgument(0 <= hour && hour <= 23, "hour must be in [0,23]");
            Preconditions.checkArgument(0 <= minute && minute <= 59, "minute must be in [0,59]");
            this.hour = hour;
            this.minute = minute;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if(!(obj instanceof Time)){
                return false;
            }
            Time other = (Time) obj;
            return hour == other.hour &&
                    minute == other.minute;
        }

        @Override
        public int hashCode() {
            return Objects.hash(hour, minute);
        }
    }
}
