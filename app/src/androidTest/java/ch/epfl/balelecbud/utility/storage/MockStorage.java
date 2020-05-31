package ch.epfl.balelecbud.utility.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.utility.InformationSource;

public class MockStorage implements Storage {

    private int accessCount = 0;

    private Map<String, Map<String, File>> storage;

    private static final MockStorage instance = new MockStorage();

    public static MockStorage getInstance() {
        return instance;
    }

    private MockStorage() {
        resetStorage();
    }

    @Override
    public CompletableFuture<File> getFile(String path) {
        String[] paths = path.split("/");
        if (paths.length != 2 || !storage.containsKey(paths[0])) {
            throw new IllegalArgumentException("Invalid path");
        }
        accessCount += 1;
        return CompletableFuture.completedFuture(storage.get(paths[0]).get(paths[1]));
    }

    @Override
    public CompletableFuture<List<String>> getAllFileNameIn(String collectionName, InformationSource source) {
        List<String> list = new ArrayList();
        if (storage.containsKey(collectionName)) {
            for (File f : storage.get(collectionName).values()) {
                list.add(collectionName + "/" + f.getName());
            }
        }
        return CompletableFuture.completedFuture(list);
    }

    @Override
    public void putFile(String collectionName, String filename, File file) {
        if (!storage.containsKey(collectionName)) {
            storage.put(collectionName, new HashMap<>());
        }
        storage.get(collectionName).put(filename, file);
    }

    private File createFile(String prefix, String suffix) {
        try {
            File tmpFile = File.createTempFile(prefix, suffix);
            InputStream inputStream = BalelecbudApplication.getAppContext()
                    .getResources().openRawResource(R.drawable.balelec_artist_pic); // id drawable
            OutputStream out = new FileOutputStream(tmpFile);
            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                out.write(buf, 0, len);
            out.close();
            inputStream.close();
            return tmpFile;
        } catch (Exception e) {
            throw new RuntimeException("Create file failed");
        }
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }

    public void resetStorage() {
        storage = new HashMap<>();
        HashMap<String, File> artistImages = new HashMap<>();
        for (int i = 1; i < 4; ++i) {
            artistImages.put("path"+i+".png", createFile("path"+i, ".png"));
        }

        HashMap<String, File> userImages = new HashMap<>();
        for (int i = 0; i < 9; ++i) {
            userImages.put("image"+i+".png", createFile("image"+i, ".png"));
        }

        HashMap<String, File> trackImages = new HashMap<>();
        trackImages.put("uri.jpeg", createFile("uri", ".jpeg"));

        storage.put(ARTISTS_IMAGES, artistImages);
        storage.put(USER_PICTURES, userImages);
        storage.put(TRACKS_IMAGES, trackImages);
    }
}
