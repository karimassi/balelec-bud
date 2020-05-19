package ch.epfl.balelecbud.utility.database;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * ADT to represent a list of object of type T and a freshness information
 * @param <T>
 */
public final class FetchedData <T> {

    //We do not make the list unmodifiable for the sake of performance
    private final List<T> list;
    private final Long freshness;

    public FetchedData(List<T> list){
        this(list, null);
    }

    public FetchedData(List<T> list, Long freshness){
        this.list = list;
        this.freshness = freshness;
    }

    public List<T> getList() {
        return list;
    }

    public Long getFreshness() {
        return freshness;
    }

    public <U> FetchedData<U> map(Function<T, U> f) {
        List<U> newList = new LinkedList<>();
        for(T elem : list){
            newList.add(f.apply(elem));
        }
        return new FetchedData<>(newList, this.freshness);
    }
}
