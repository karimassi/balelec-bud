package ch.epfl.balelecbud.utility.storage;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MockCache implements Cache{

    List<String> storedFiles = new LinkedList<>();

    @Override
    public boolean contains(String name) {
        return  storedFiles.contains(name);
    }

    @Override
    public CompletableFuture<File> get(String name) {
        return null;
    }

    @Override
    public File put(File file, String name) {
        storedFiles.add(name);
        return null;
    }

    @Override
    public void flush(){
        storedFiles.clear();
    }
}
