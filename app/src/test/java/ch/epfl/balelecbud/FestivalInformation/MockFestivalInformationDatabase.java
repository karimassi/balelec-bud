package ch.epfl.balelecbud.FestivalInformation;

import java.util.ArrayList;
import java.util.List;

public class MockFestivalInformationDatabase implements BasicDatabase {


    private FestivalInformationListener listener;
    private List<FestivalInformation> informationList;

    public MockFestivalInformationDatabase() {
        informationList = new ArrayList<>();
    }

    public void addInformation(FestivalInformation info) {
        informationList.add(info);
        listener.addInformation(info);
    }

    public void modifyInformation(FestivalInformation info, int index) {
        if (index < informationList.size())
            informationList.set(index, info);
        listener.modifyInformation(info, index);
    }


    public void removeInformation(FestivalInformation info) {
        int index = informationList.indexOf(info);
        informationList.remove(info);
        listener.removeInformation(info, index);
    }


    @Override
    public void registerListener(FestivalInformationListener listener) {
        this.listener = listener;
    }

    @Override
    public void unregisterListener() {

    }

    @Override
    public void listen() {

    }
}
