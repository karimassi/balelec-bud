package ch.epfl.balelecbud.FestivalInformation;

public interface BasicDatabase {

    void registerListener(FestivalInformationListener listener);
    void unregisterListener();
    void listen();
}
