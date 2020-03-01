package ch.epfl.sdp.schedule;

import java.util.List;

public interface AbstractScheduleProvider {
    public List<Artist> getSceneSchedule(String sceneName);
    public void getScenePicture(String sceneName);  //Should not actually return void, return time
                                                    //will depend on how we choose to store the pic
                                                    // as a BlekScene attribute
    public TimeSlot getArtistTimeSlot(String artistName);
    public void getArtistPicture(String artistName);
}
