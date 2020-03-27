package ch.epfl.balelecbud.util;


import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.CompletableFuture;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TaskToCompletableFutureAdapter<T> extends CompletableFuture<T> {

    public TaskToCompletableFutureAdapter(Task<T> result) {
        super();
        result.addOnSuccessListener(new OnSuccessListener<T>() {
            @Override
            public void onSuccess(T t) {
                complete(t);
            }
        });
        result.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                completeExceptionally(e);
            }
        });
    }

}
