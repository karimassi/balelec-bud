package ch.epfl.balelecbud.util.storage;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.testUtils.TestAsyncUtils;

public class CachedStorageTest {

    @Test
    public void innerStorageAccessedWhenCacheEmpty(){
        TestAsyncUtils sync = new TestAsyncUtils();
        Storage mockStorage = path -> {
            sync.call();
            try {
                return CompletableFuture.completedFuture(File.createTempFile("test", path));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        };
        Storage cached = new CachedStorage(mockStorage, new MockCache());
        cached.getFile("randomName");
        sync.assertCalled(1);
    }

    @Test
    public void innerStorageNotAccessedWhenCacheFull(){
        TestAsyncUtils sync = new TestAsyncUtils();
        Storage mockStorage = path -> {
            sync.call();
            try {
                return CompletableFuture.completedFuture(File.createTempFile("test", path));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        };
        MockCache mockCache = new MockCache();
        mockCache.storedFiles.add("randomName");
        Storage cached = new CachedStorage(mockStorage, mockCache);
        cached.getFile("randomName");
        sync.assertCalled(0);
    }

}
