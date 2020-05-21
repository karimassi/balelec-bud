package ch.epfl.balelecbud.utility.json;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.Gson;


import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.model.HelpPage;

/**
 * Simple static class that is given a raw resource ID and can convert the file to an instance of
 * of a given class or a collection of that particular class
 */
public final class JsonResourceReader {

    private static final Gson gson = new Gson();

    private static JsonReader getReader(int resId){
        return new JsonReader(
                new InputStreamReader(
                        BalelecbudApplication.getAppContext().getResources().openRawResource(resId)));

    }

    static <T> T getObject(int resId, T target) {
        return gson.fromJson(getReader(resId), target.getClass());
    }

    /**
     * Return a list of help pages, sadly cannot be generic because of Java Type Erasure
     * @param resId
     * @return
     */
    static List<HelpPage> getHelpPageCollection(int resId) {
        Type listType = new TypeToken<ArrayList<HelpPage>>(){}.getType();
        return gson.fromJson(getReader(resId), listType);
    }
}
