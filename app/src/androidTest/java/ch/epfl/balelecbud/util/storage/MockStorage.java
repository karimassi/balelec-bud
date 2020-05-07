package ch.epfl.balelecbud.util.storage;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.testUtils.TestAsyncUtils;

public class MockStorage implements Storage {

    private int accessCount = 0;

    @Override
    public CompletableFuture<File> getFile(String path) {
        accessCount += 1;
        CompletableFuture<File> future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalAccessError());
        return future;
    }


    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }
}
