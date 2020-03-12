package ch.epfl.balelecbud.festivalInformation;

import java.util.List;

public class FestivalInformationListener {

    private static FestivalInformationAdapterFacade facade;
    private static List<FestivalInformation> informationList;

    public FestivalInformationListener(FestivalInformationAdapterFacade facade, List<FestivalInformation> informationList) {
        this.facade = facade;
        this.informationList = informationList;
    }

    public static void addInformation(FestivalInformation information) {
        informationList.add(information);
        facade.notifyItemInserted(informationList.size()-1);
    }

    public static void modifyInformation(FestivalInformation information, int index) {
        if (index < informationList.size()) {
            informationList.set(index, information);
            facade.notifyItemModified(index);
        }
    }

    public static void removeInformation(FestivalInformation information, int index) {
        if (informationList.contains(information)) {
            informationList.remove(information);
            facade.notifyItemRemoved(index);
        }
    }

}
