package ch.epfl.balelecbud.util.cache;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.util.database.MyQuery;

public class FileSystemCache implements Cache {

    Gson gson;
    Context context;

    public FileSystemCache() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        context = BalelecbudApplication.getAppContext();
    }

    @Override
    public boolean contains(MyQuery query) {
        Context context = BalelecbudApplication.getAppContext();
        File cacheFile = new File(context.getCacheDir(), query.getCollectionName());
        if (cacheFile.exists()) {
            if (query.getGeoClause() != null) {
                return false;
            }
            if (query.getWhereClauses().isEmpty()) return true;
            if (query.hasDocumentIdOperand()) {
                File requestedFile = new File(cacheFile, query.getIdOperand());
                return requestedFile.exists();
            }
            throw new UnsupportedOperationException();
        }
        return false;
    }

    private File[] getMatchingFiles(MyQuery query) {
        Context context = BalelecbudApplication.getAppContext();
        File cacheFile = new File(context.getCacheDir(), query.getCollectionName());
        FilenameFilter filenameFilter = (dir, name) -> {
            if (query.hasDocumentIdOperand()) return name.equals(query.getIdOperand());
            else return true;
        };
        return cacheFile.listFiles(filenameFilter);
    }

    @Override
    public <T> CompletableFuture<List<T>> get(MyQuery query, Class<T> tClass) throws IOException {
        List<T> items = new ArrayList<>();
        for (File file : getMatchingFiles(query)) {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            items.add(gson.fromJson(isr, tClass));
            isr.close();
            fis.close();
        }
        return CompletableFuture.completedFuture(items);
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> get(MyQuery query) throws IOException {
        List<Map<String, Object>> items = new ArrayList<>();
        for (File file : getMatchingFiles(query)) {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            items.add(gson.fromJson(isr, type));
            isr.close();
            fis.close();
        }
        return CompletableFuture.completedFuture(items);
    }

    @Override
    public void put(String collectionName, String id, Object document) throws IOException {
        File cacheFile = new File(context.getCacheDir(), collectionName);
        cacheFile.mkdir();
        if (!cacheFile.exists()) {
            cacheFile = new File(context.getFilesDir(), collectionName);
        }
        File toStore = new File(cacheFile, id);
        FileOutputStream fos = new FileOutputStream(toStore);
        OutputStreamWriter osw =new OutputStreamWriter(fos);
        gson.toJson(document, osw);
        osw.close();
        fos.close();
    }

    @Override
    public void flush(String collectionName) {
        File cacheFile = new File(context.getCacheDir(), collectionName);
        if (cacheFile.isDirectory()) {
            for (File file : cacheFile.listFiles()) {
                file.delete();
            }
        }
        cacheFile.delete();
    }


}
