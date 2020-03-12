package ch.epfl.balelecbud.FestivalInformation;

public class FestivalInformationProvider {

    BasicDatabase db;

    public void subscribe(FestivalInformationListener listener, BasicDatabase db) {
        this.db = db;
        db.registerListener(listener);
        db.listen();
    }

    public void unsubscribe() {
        db.unregisterListener();
    }

}
