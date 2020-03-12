package ch.epfl.balelecbud.festivalInformation;

public interface BasicDatabase {

    void registerListener(FestivalInformationListener listener);
    void unregisterListener();
    void listen();
}
