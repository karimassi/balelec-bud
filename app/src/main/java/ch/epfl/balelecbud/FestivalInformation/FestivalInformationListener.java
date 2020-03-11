package ch.epfl.balelecbud.FestivalInformation;

import java.util.ArrayList;
import java.util.List;

public class FestivalInformationListener {

    private FestivalInformationAdapterFacade facade;
    private List<FestivalInformation> informationList;

    public FestivalInformationListener(FestivalInformationAdapterFacade facade, List<FestivalInformation> informationList) {
        this.facade = facade;
        this.informationList = informationList;
    }

    public void addInformation(FestivalInformation information, int index) {
        informationList.add(index, information);
        facade.notifyItemInserted(index);
    }

    public void modifyInformation(FestivalInformation information, int index) {
        if (informationList.contains(information)) {
            informationList.set(index, information);
            facade.notifyItemModified(index);
        } else {
            addInformation(information, index);
        }
    }

    public void removeInformation(FestivalInformation information, int index) {
        if (informationList.contains(information)) {
            informationList.remove(information);
            facade.notifyItemRemoved(index);
        }
    }

}
