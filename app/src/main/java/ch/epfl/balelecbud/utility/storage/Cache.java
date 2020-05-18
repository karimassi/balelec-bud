package ch.epfl.balelecbud.utility.storage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Interface modeling a cache for files
 */
public interface Cache {

    /**
     * Check if the cache contains a given file
     *
     * @param name the name of the file
     * @return     {@code true} if the cache contains that file, otherwise {@code false}
     */
    boolean contains(String name);

    /**
     * Load a given file from the cache
     *
     * @param name the name of the file to retrieve
     * @return     a {@code CompletableFuture} that will complete with the requested file
     */
    CompletableFuture<File> get(String name);

    /**
     * Store a file in the cache
     *
     * @param file the file to store
     * @param name the name of the file
     * @return     the file stored in the cache
     * @throws IOException if an error occurred
     */
    File put(File file, String name) throws IOException;

    /**
     * Flush the cache of all this files
     */
    void flush();
}
