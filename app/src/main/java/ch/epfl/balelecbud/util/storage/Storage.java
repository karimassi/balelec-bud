package ch.epfl.balelecbud.util.storage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    CompletableFuture<File> getFile(String path);

}
