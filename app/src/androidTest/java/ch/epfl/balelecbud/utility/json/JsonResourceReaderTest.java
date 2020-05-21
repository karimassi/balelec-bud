package ch.epfl.balelecbud.utility.json;

import org.junit.Test;

import java.util.List;

import ch.epfl.balelecbud.R;
import ch.epfl.balelecbud.model.HelpPage;

import static org.testng.AssertJUnit.assertEquals;


public class JsonResourceReaderTest {

    private String expectedTitle1 = "the title of the help page";
    private String expectedDescription1 = "what is without doubt an amazing description of the feature";
    private int expectedId1 = 1;


    @Test
    public void getObjectReturnsExpectedResult(){
        HelpPage result = JsonResourceReader.getObject(R.raw.help_page_test, new HelpPage());
        assertEquals(expectedTitle1, result.getTitle());
        assertEquals(expectedDescription1, result.getDescription());
        assertEquals(expectedId1, result.getId());
    }

    @Test
    public void getHelpPageCollectFromJsonReturnsExpectedResult(){
        List<HelpPage> result = JsonResourceReader.getHelpPageCollection(R.raw.help_page_list_test);
        assertEquals(2, result.size());

        HelpPage res1 = result.get(0);

        assertEquals(expectedTitle1, result.get(0).getTitle());
        assertEquals(expectedDescription1, result.get(0).getDescription());
        assertEquals(expectedId1, result.get(0).getId());


        String expectedTitle2 = "another great title";
        String expectedDescription2 = "a superb description";
        int expectedId2 = 2;
        
        assertEquals(expectedTitle2, result.get(1).getTitle());
        assertEquals(expectedDescription2, result.get(1).getDescription());
        assertEquals(expectedId2, result.get(1).getId());

    }
}
