package ch.epfl.balelecbud.schedule;

import java.util.List;

import ch.epfl.balelecbud.schedule.models.Band;

public interface AbstractScheduleProvider {

    void subscribeConcerts(List<Concert> concerts);

    Scene getSceneFromId(Scene.Id id);

    Concert getConcertFromId(Concert.Id id);

    List<Band.Id> getBandIds();

    List<Scene.Id> getSceneIds();

    List<Concert.Id> getConcertIds();

}
