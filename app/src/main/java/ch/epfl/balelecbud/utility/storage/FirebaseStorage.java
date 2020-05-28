package ch.epfl.balelecbud.utility.storage;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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

    private static final String TAG = FirebaseStorage.class.getSimpleName();

    private static final FirebaseStorage instance = new FirebaseStorage();
    private final com.google.firebase.storage.FirebaseStorage firebaseStorage = com.google.firebase.storage.FirebaseStorage.getInstance();

    private FirebaseStorage() {}

    public static FirebaseStorage getInstance() { return instance; }

    @Override
    public CompletableFuture<File> getFile(String path) {
        CompletableFuture<File> future = new CompletableFuture<>();

        StorageReference storageRef = firebaseStorage.getReference();
        try {
            final File localFile = File.createTempFile("images", "jpg");
            future = new TaskToCompletableFutureAdapter<>(storageRef.child(path).getFile(localFile)).thenApply(x -> localFile);
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<List<String>> getAllFileNameIn(String collectionName, InformationSource source) {
        StorageReference ref = firebaseStorage.getReference().child(collectionName);
        Log.d(TAG, "getAllFileNameIn " + collectionName);
        return new TaskToCompletableFutureAdapter<>(ref.listAll())
                .thenApply(listResult ->
                        listResult.getItems().stream().map(StorageReference::getPath).collect(Collectors.toList()));
    }

    @Override
    public void putFile(String collectionName, String filename, File file){
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference picRef = storageRef.child(collectionName + "/" + filename);
        picRef.putFile(Uri.fromFile(file));
    }
}

