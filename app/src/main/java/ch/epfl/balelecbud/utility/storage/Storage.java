package ch.epfl.balelecbud.utility.storage;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    CompletableFuture<File> getFile(String path);

}
