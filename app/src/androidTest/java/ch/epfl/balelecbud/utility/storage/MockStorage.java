package ch.epfl.balelecbud.utility.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.R;

public class MockStorage implements Storage {

    private int accessCount = 0;
    private File fileToReturn = createFile();

    @Override
    public CompletableFuture<File> getFile(String path) {
        accessCount += 1;
        CompletableFuture<File> future = new CompletableFuture<>();
        future.complete(fileToReturn);
        return future;
    }

    private File createFile() {
        try {
            File tmpFile = File.createTempFile("mockFile", "png");
            InputStream inputStream = BalelecbudApplication.getAppContext()
                    .getResources().openRawResource(R.drawable.balelec_artist_pic); // id drawable
            OutputStream out = new FileOutputStream(tmpFile);
            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                out.write(buf, 0, len);
            out.close();
            inputStream.close();
            return tmpFile;
        } catch (Exception e) {
            throw new RuntimeException("Create file failed");
        }
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }
}
