package ch.epfl.balelecbud.utility.storage;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import ch.epfl.balelecbud.utility.InformationSource;
import ch.epfl.balelecbud.utility.TaskToCompletableFutureAdapter;

/**
 * A Firebase storage adapter
 */
public final class FirebaseStorage implements Storage {

    private static final FirebaseStorage instance = new FirebaseStorage();
    private final StorageReference firebaseStorageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference();

    private FirebaseStorage() {}

    public static FirebaseStorage getInstance() { return instance; }

    @Override
    public CompletableFuture<File> getFile(String path) {
        CompletableFuture<File> future = new CompletableFuture<>();
        try {
            final File localFile = File.createTempFile("images", "jpg");
            future = new TaskToCompletableFutureAdapter<>(firebaseStorageRef.child(path).getFile(localFile)).thenApply(x -> localFile);
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<List<String>> getAllFileNameIn(String collectionName, InformationSource source) {
        return new TaskToCompletableFutureAdapter<>(firebaseStorageRef.child(collectionName).listAll())
                .thenApply(listResult -> listResult.getItems().stream().map(StorageReference::getPath).collect(Collectors.toList()));
    }

    @Override
    public void putFile(String collectionName, String filename, File file){
        firebaseStorageRef.child(collectionName + "/" + filename).putFile(Uri.fromFile(file));
    }
}

