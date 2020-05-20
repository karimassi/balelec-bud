package ch.epfl.balelecbud.utility.storage;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.testUtils.FileUtils;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.InformationSource;

import static junit.framework.TestCase.assertNotNull;

public class CachedStorageTest {

    @Test
    public void innerStorageAccessedWhenCacheEmpty(){
        TestAsyncUtils sync = new TestAsyncUtils();
        Storage mockStorage = new MockStorages(sync);
        Storage cached = new CachedStorage(mockStorage, new MockCache());
        cached.getFile("randomName");
        sync.assertCalled(1);
    }

    @Test
    public void innerStorageNotAccessedWhenCacheFull(){
        TestAsyncUtils sync = new TestAsyncUtils();
        Storage mockStorage = new MockStorages(sync);
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
        Storage mockStorage = new Storage() {
            @Override
            public CompletableFuture<File> getFile(String path) {
                sync.call();
                try {
                    return CompletableFuture.completedFuture(FileUtils.createFileWithContent(expectedContent, "fileName"));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }            }

            @Override
            public CompletableFuture<List<String>> getAllFileNameIn(String collectionName, InformationSource source) {
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

    private static class MockStorages implements Storage {
        TestAsyncUtils sync;

        MockStorages(TestAsyncUtils sync) {
            this.sync = sync;
        }

        @Override
        public CompletableFuture<File> getFile(String path) {
            sync.call();
            try {
                return CompletableFuture.completedFuture(File.createTempFile("test", path));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public CompletableFuture<List<String>> getAllFileNameIn(String collectionName, InformationSource source) {
            return null;
        }
    }

}
