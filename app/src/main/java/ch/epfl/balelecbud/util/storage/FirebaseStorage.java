package ch.epfl.balelecbud.util.storage;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class FirebaseStorage implements Storage {
    @Override
    public CompletableFuture<File> getFile(String path) {
        return null;
    }
}
