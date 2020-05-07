package ch.epfl.balelecbud.util.storage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface Cache {

    boolean contains(String name);

    CompletableFuture<File> get(String name);

    File put(File file, String name) throws IOException;

    void flush();
}
