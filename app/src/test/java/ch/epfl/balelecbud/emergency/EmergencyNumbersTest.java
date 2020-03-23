package ch.epfl.balelecbud.emergency;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.balelecbud.emergency.models.EmergencyNumbers;

import static org.hamcrest.core.Is.is;

public class EmergencyNumbersTest {
    private EmergencyNumbers eN1 = new EmergencyNumbers("Firefighters","118");

    @Test
    public void testGetInstruction() {
        Assert.assertThat(eN1.getName(), is("Firefighters"));
    }

    @Test
    public void testGetName() {
        Assert.assertThat(eN1.getNumber(), is("118"));
    }

}
