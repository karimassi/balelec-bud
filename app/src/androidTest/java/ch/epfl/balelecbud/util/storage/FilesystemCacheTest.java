package ch.epfl.balelecbud.util.storage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import ch.epfl.balelecbud.testUtils.FileUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FilesystemCacheTest {

    private static FilesystemCache fsCache;

    @BeforeClass
    public static void createFsCache() {
        fsCache = new FilesystemCache();
    }

    @Before
    public void setup() {
        fsCache.flush();
    }

    @Test
    public void flushDeletesFiles() throws IOException {
        String fileName = "whatever";
        fsCache.put(File.createTempFile("tmpPrefix", "tmpFile"), fileName);
        assertTrue(fsCache.contains(fileName));
        fsCache.flush();
        assertFalse(fsCache.contains(fileName));
    }

    @Test
    public void containsWorksCorrectly() throws IOException {
        String fileName = "whatever";
        assertFalse(fsCache.contains(fileName));
        fsCache.put(File.createTempFile("tmpPrefix", "tmpFile"), fileName);
        assertTrue(fsCache.contains(fileName));
    }

    @Test
    public void canReadFilePutInPreviously() throws IOException {
        String fileName = "whatever";
        String expected = "This is the content of a file";
        File tmp = FileUtils.createFileWithContent(expected, fileName);

        fsCache.put(tmp, fileName);
        File result = fsCache.get(fileName).getNow(null);
        assertNotNull(result);

        assertTrue(FileUtils.checkContent(result, expected));
    }
}
