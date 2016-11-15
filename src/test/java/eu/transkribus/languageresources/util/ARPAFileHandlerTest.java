package eu.transkribus.languageresources.util;

import eu.transkribus.languageresources.exceptions.ARPAParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jnphilipp
 */
public class ARPAFileHandlerTest {
    @Test
    public void testReadWrite() throws ARPAParseException, FileNotFoundException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(new File(classLoader.getResource(".").getFile()).getAbsolutePath() + "/test.arpa");

        Map<Integer, Map<List<String>, Map<String, Double>>> ngrams = new LinkedHashMap<>();

        Map<List<String>, Map<String, Double>> n1grames = new LinkedHashMap<>();
        Map<String, Double> p = new LinkedHashMap<>();
        p.put("", 0.5);
        n1grames.put(Arrays.asList(new String[] {"Haus"}), p);
        p = new LinkedHashMap<>();
        p.put("", 0.33);
        n1grames.put(Arrays.asList(new String[] {"das"}), p);
        p = new LinkedHashMap<>();
        p.put("", 0.33);
        n1grames.put(Arrays.asList(new String[] {"dem"}), p);
        ngrams.put(1, n1grames);

        Map<List<String>, Map<String, Double>> n2grames = new LinkedHashMap<>();
        p = new LinkedHashMap<>();
        p.put("Haus", 0.5);
        n2grames.put(Arrays.asList(new String[] {"das"}), p);
        p = new LinkedHashMap<>();
        p.put("Haus", 0.5);
        n2grames.put(Arrays.asList(new String[] {"dem"}), p);
        ngrams.put(2, n2grames);

        ARPAFileHandler.write(file, ngrams);
        Map<Integer, Map<List<String>, Map<String, Double>>> ngrams2 = ARPAFileHandler.read(file);
        assertEquals(ngrams, ngrams2);
    }
}
