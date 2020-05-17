package ch.epfl.balelecbud.utility.storage;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.utility.TaskToCompletableFutureAdapter;

/**
 * A Firebase storage adapter
 */
public final class FirebaseStorage implements Storage {

    private static final String TAG = FirebaseStorage.class.getSimpleName();

    private static final FirebaseStorage instance = new FirebaseStorage();

    private FirebaseStorage() {}

    public static FirebaseStorage getInstance() { return instance; }

    @Override
    public CompletableFuture<File> getFile(String path) {

        CompletableFuture<File> future = new CompletableFuture<>();

        StorageReference storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference();
        try {
            final File localFile = File.createTempFile("images", "jpg");
            future = new TaskToCompletableFutureAdapter<FileDownloadTask.TaskSnapshot>(storageRef.child(path).getFile(localFile)).thenApply(x -> localFile);
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        return future;
    }
}
