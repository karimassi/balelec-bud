package ch.epfl.balelecbud.util.database;

import android.util.Log;

import java.util.List;

import ch.epfl.balelecbud.util.adapters.RecyclerViewAdapterFacade;

public class DatabaseListener<T> {

    private RecyclerViewAdapterFacade facade; // chnage type later
    private List<T> dataList;
    private Class<T> type;

    public DatabaseListener(RecyclerViewAdapterFacade facade, List<T> dataList, Class<T> type) {
        this.facade = facade;
        this.dataList = dataList;
        this.type = type;
    }

    public void onItemAdded(T data) {
        dataList.add(data);
        facade.notifyItemInserted(dataList.size()-1);
    }

    public void onItemChanged(T data, int index) {
        if (index < dataList.size()) {
            dataList.set(index, data);
            facade.notifyItemChanged(index);
        }
    }

    public void onItemRemoved(T data, int index) {
        if (dataList.indexOf(data) == index) {
            dataList.remove(data);
            facade.notifyItemRemoved(index);
        }
    }

    public Class<T> getType() {
        return type;
    }

}
