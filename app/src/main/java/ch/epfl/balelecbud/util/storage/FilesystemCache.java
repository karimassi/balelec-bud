package ch.epfl.balelecbud.util.storage;

import android.content.Context;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.google.common.io.Files;

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

    public FilesystemCache(){
        cacheDir = getCacheDirectory();
    }

    private File getCacheDirectory(){
        Context ctx = BalelecbudApplication.getAppContext();
        File dir = new File(ctx.getFilesDir(), "cache");
        if(!dir.exists() && !dir.mkdir()){
                Log.w(TAG, "could not create cache directory");
        }
        File artistsImagesDir = new File(dir, "artists_images");
        if(!artistsImagesDir.exists() && !artistsImagesDir.mkdir()){
                Log.w(TAG, "could not create cache/artists_images directory");
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

        try{
            Files.move(file, newFile);
            return newFile;
        } catch (IOException e) {
            Log.w(TAG, "could not move file");
            Log.w(TAG, e.getMessage());
            return file;
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
