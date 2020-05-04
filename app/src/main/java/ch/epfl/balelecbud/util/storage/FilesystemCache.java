package ch.epfl.balelecbud.util.storage;

import android.content.Context;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;

public class FilesystemCache implements Cache {

    private static final String TAG = FilesystemCache.class.getSimpleName();
    private File cacheDir;

    FilesystemCache(){
        cacheDir = getCacheDirectory();
    }

    @VisibleForTesting
    public File getCacheDirectory(){
        Context ctx = BalelecbudApplication.getAppContext();
        File dir = new File(ctx.getFilesDir(), "cache");
        if(!dir.exists()){
            if(!dir.mkdir()){
                //TODO throw an exception and catch it in CachedStorage, then we do not try
                //     to access the cache
                Log.w(TAG, "could not create cache directory");
            }
        }
        return dir;
    }

    @Override
    public boolean contains(String name) {
        return (new File(cacheDir, name)).exists();
    }

    @Override
    public CompletableFuture<File> get(String name) {
        return CompletableFuture.completedFuture(new File(cacheDir, name));
    }

    @Override
    public File put(File file, String name) {
        File newFile = new File(cacheDir, name);
        if (file.renameTo(newFile)) {
            return newFile;
        } else {
            throw new IOError(new IOException("could not rename file"));
        }
    }

    @Override
    public void flush(){
        List<File> files = Arrays.asList(cacheDir.listFiles());
        for(File f : files){
            f.delete();
        }
    }
}
