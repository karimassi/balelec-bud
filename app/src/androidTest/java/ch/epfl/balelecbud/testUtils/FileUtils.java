package ch.epfl.balelecbud.testUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FileUtils {

    public static File createFileWithContent(String content, String name) throws IOException {
        char[] charArray = content.toCharArray();
        File tmp = File.createTempFile("tmpPrefix", name);

        FileWriter writer = new FileWriter(tmp);
        writer.write(charArray);
        writer.close();

        return tmp;
    }

    public static boolean checkContent(File file, String expectedContent) throws IOException {
        FileReader reader = new FileReader(file);
        char[] contentResult = new char[expectedContent.length()];
        assertEquals(reader.read(contentResult), expectedContent.length());

        return expectedContent.equals(new String(contentResult));
    }

}
