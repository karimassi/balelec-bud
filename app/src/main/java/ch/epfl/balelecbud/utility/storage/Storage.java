package ch.epfl.balelecbud.utility.storage;

import android.graphics.Bitmap;

import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.utility.InformationSource;

/**
 * Interface modeling a file storage
 */
public interface Storage {

    String USER_PICTURES = "users_pictures";
    String ARTISTS_IMAGES = "artists_images";
    String TRACKS_IMAGES = "tracks_images";

    CompletableFuture<File> getFile(String path);

    CompletableFuture<List<String>> getAllFileNameIn(String collectionName, InformationSource source);

    void putFile(String collectionName, String filename, File file) throws IOException;

}
