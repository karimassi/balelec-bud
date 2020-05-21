package ch.epfl.balelecbud.utility.storage;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.utility.InformationSource;

/**
 * Interface modeling a file storage
 */
public interface Storage {

    String USER_PICTURE = "users_pictures";

    CompletableFuture<File> getFile(String path);

    CompletableFuture<List<String>> getAllFileNameIn(String collectionName, InformationSource source);

}
