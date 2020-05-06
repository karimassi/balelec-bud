package ch.epfl.balelecbud.util.storage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
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
        String stringContent = "This is the content of a file";
        char[] content = stringContent.toCharArray();
        File tmp = File.createTempFile("tmpPrefix", "tmpFile");

        FileWriter writer = new FileWriter(tmp);
        writer.write(content);
        writer.close();

        fsCache.put(tmp, fileName);
        File result = fsCache.get(fileName).getNow(null);
        assertNotNull(result);

        FileReader reader = new FileReader(result);
        char[] contentResult = new char[stringContent.length()];
        assertEquals(reader.read(contentResult), stringContent.length());

        assertArrayEquals(content, contentResult);
    }
}
