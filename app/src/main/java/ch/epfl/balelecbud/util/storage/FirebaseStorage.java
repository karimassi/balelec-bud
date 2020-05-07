package ch.epfl.balelecbud.util.storage;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class FirebaseStorage implements Storage {

    private static final String TAG = FirebaseStorage.class.getSimpleName();

    private static final FirebaseStorage instance = new FirebaseStorage();

    private FirebaseStorage() {}

    public static FirebaseStorage getInstance() { return instance; }

    @Override
    public CompletableFuture<File> getFile(String path) {

        CompletableFuture<File> future = new CompletableFuture<>();

        try {
            StorageReference storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference();
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.child(path).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.w(TAG, "Download of " + path + " succeeded");
                    future.complete(localFile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.w(TAG, "Download of " + path + " failed");
                    future.completeExceptionally(exception);
                }
            });
        } catch (IOException e) {
            future.completeExceptionally(e);
        }

        return future;
    }
}
