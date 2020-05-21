package ch.epfl.balelecbud.utility.json;

import org.junit.Test;

import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.HelpPage;

import static org.testng.AssertJUnit.assertEquals;


public class JsonResourceReaderTest {

    private String expectedImageName1 = "the_name_of_the_image";
    private String expectedTitle1 = "the title of the help page";
    private String expectedDescription1 = "what is without doubt an amazing description of the feature";


    @Test
    public void getObjectReturnsExpectedResult(){
        HelpPage result = JsonResourceReader.getObject(R.raw.help_page_test, new HelpPage());
        assertEquals(expectedImageName1, result.getImageName());
        assertEquals(expectedTitle1, result.getTitle());
        assertEquals(expectedDescription1, result.getDescription());
    }

    @Test
    public void getHelpPageCollectFromJsonReturnsExpectedResult(){
        List<HelpPage> result = JsonResourceReader.getHelpPageCollection(R.raw.help_page_list_test);
        assertEquals(2, result.size());

        assertEquals(expectedImageName1, result.get(0).getImageName());
        assertEquals(expectedTitle1, result.get(0).getTitle());
        assertEquals(expectedDescription1, result.get(0).getDescription());

        String expectedImageName2 = "another_image_name";
        String expectedTitle2 = "another great title";
        String expectedDescription2 = "a superb description";

        assertEquals(expectedImageName2, result.get(1).getImageName());
        assertEquals(expectedTitle2, result.get(1).getTitle());
        assertEquals(expectedDescription2, result.get(1).getDescription());

    }
}
