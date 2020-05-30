package ch.epfl.balelecbud.utility.storage;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.balelecbud.testUtils.FileUtils;
import ch.epfl.balelecbud.testUtils.TestAsyncUtils;
import ch.epfl.balelecbud.utility.CompletableFutureUtils;
import ch.epfl.balelecbud.utility.InformationSource;

import static ch.epfl.balelecbud.BalelecbudApplication.setRemoteStorage;
import static junit.framework.TestCase.assertNotNull;

public class CachedStorageTest {

    @Test
    public void innerStorageAccessedWhenCacheEmpty(){
        TestAsyncUtils sync = new TestAsyncUtils();
        Storage mockStorage = new MockStorages(sync);
        setRemoteStorage(mockStorage);
        Storage cached = new CachedStorage(new MockCache());
        cached.getFile("randomName");
        sync.assertCalled(1);
    }

    @Test
    public void innerStorageNotAccessedWhenCacheFull(){
        TestAsyncUtils sync = new TestAsyncUtils();
        Storage mockStorage = new MockStorages(sync);
        setRemoteStorage(mockStorage);
        MockCache mockCache = new MockCache();
        mockCache.storedFiles.add("randomName");
        Storage cached = new CachedStorage(mockCache);
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

            @Override
            public void putFile(String collectionName, String filename, File file) {

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
        setRemoteStorage(mockStorage);
        CachedStorage cachedStorage = new CachedStorage(mockCache);
        File resultFile = cachedStorage.getFile("any filename will do").getNow(null);
        assertNotNull(resultFile);
        FileUtils.checkContent(resultFile, expectedContent);

    }

    @Test
    public void getAllFileNamesReturnsEmptyListOnCacheOnly() throws Throwable {
        TestAsyncUtils sync = new TestAsyncUtils();
        Storage mockStorage = new MockStorages(sync);
        setRemoteStorage(mockStorage);
        Storage cached = new CachedStorage(new MockCache());
        CompletableFuture<List<String>> res = cached.getAllFileNameIn(Storage.USER_PICTURES, InformationSource.CACHE_ONLY);
        res.whenComplete((strings, throwable) -> {
            if (throwable != null) sync.fail();
            else {
                sync.assertTrue(strings.isEmpty());
                sync.call();
            }
        });
        sync.waitCall(1);
        sync.assertCalled(1);
        sync.assertNoFailedTests();
    }

    @Test
    public void storageCanPutAndRetrieveFilesCorrectly() throws IOException {
        MockStorage mockStorage = MockStorage.getInstance();
        String filename = "Any filename will do";
        setRemoteStorage(MockStorage.getInstance());
        File inputFile = FileUtils.createFileWithContent("any content will do", filename);
        mockStorage.putFile("any collection", filename, inputFile);
        File resultFile = mockStorage.getFile("any collection/" + filename).getNow(null);
        assertNotNull(resultFile);
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

        @Override
        public void putFile(String collectionName, String filename, File file) {

        }
    }

}
