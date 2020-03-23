package ch.epfl.balelecbud.emergency;


import org.junit.Assert;
import org.junit.Test;

import ch.epfl.balelecbud.emergency.models.EmergencyInfo;


import static org.hamcrest.core.Is.is;

public class EmergencyInfoTest {
    private EmergencyInfo eI1 = new EmergencyInfo("Bad weather","Go under the roofs");

    @Test
    public void testGetInstruction() {
        Assert.assertThat(eI1.getInstruction(), is("Go under the roofs"));
    }

    @Test
    public void testGetName() {
        Assert.assertThat(eI1.getName(), is("Bad weather"));
    }

}
