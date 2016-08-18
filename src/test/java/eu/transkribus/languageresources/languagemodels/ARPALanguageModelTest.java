package eu.transkribus.languageresources.languagemodels;

import eu.transkribus.languageresources.exceptions.ARPAParseException;
import eu.transkribus.languageresources.exceptions.UnsupportedSequenceException;
import eu.transkribus.languageresources.languagemodels.ARPALanguageModel;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnphilipp
 */
public class ARPALanguageModelTest {
    @Test
    public void testGetProbabilityForNextToken() throws ARPAParseException, IOException, UnsupportedSequenceException {
        ARPALanguageModel lm = new ARPALanguageModel(new File(this.getClass().getClassLoader().getResource("lm_bozen_7_characters.arpa").getFile()).getAbsolutePath());

        Map<String, Double> expected = new LinkedHashMap<>();
        expected.put("B", 3.36673065249E-6);
        expected.put("h", 2.43306145421E-4);
        expected.put("F", 2.03565647894E-7);
        Map<String, Double> confMat = lm.getProbabilitiesForNextToken(Arrays.asList("B", "B", "B", "B", "B", "B"));
        assertEquals(expected, confMat);

        expected = new LinkedHashMap<>();
        expected.put("B", 1.03402451046E-8);
        expected.put("h", 7.09022206138E-5);
        expected.put("F", 2.41181208338E-8);
        confMat = lm.getProbabilitiesForNextToken(Arrays.asList("F", "F", "h", "h", "B", "h"));
        assertEquals(expected, confMat);
    }
}
