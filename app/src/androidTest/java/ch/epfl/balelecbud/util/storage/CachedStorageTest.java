package ch.epfl.balelecbud.util.storage;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.testUtils.FileUtils;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;

import static junit.framework.TestCase.assertNotNull;

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

    @Test
    public void storageStillReturnsImageWhenCacheFails() throws IOException {
        TestAsyncUtils sync = new TestAsyncUtils();
        String expectedContent = "some random content";
        Storage mockStorage = path -> {
            sync.call();
            try {
                return CompletableFuture.completedFuture(FileUtils.createFileWithContent(expectedContent, "fileName"));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        };
        Cache mockCache = new Cache() {
            @Override
            public boolean contains(String name) {
                return false;
            }

            @Override
            public CompletableFuture<File> get(String name) {
                return null;
            }

            @Override
            public File put(File file, String name) throws IOException {
                throw new IOException();
            }

            @Override
            public void flush() {

            }
        };

        CachedStorage cachedStorage = new CachedStorage(mockStorage, mockCache);
        File resultFile = cachedStorage.getFile("any filename will do").getNow(null);
        assertNotNull(resultFile);
        FileUtils.checkContent(resultFile, expectedContent);

    }

}
