/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml;

import eu.transkribus.languageresources.interfaces.IDictionary;
import java.io.File;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author max
 */
public class PAGEXMLAbbreviationsTest
{

    private final PAGEXMLExtractor extractor_simple;
    private final PAGEXMLExtractor extractor_config;

    public PAGEXMLAbbreviationsTest()
    {
        extractor_simple = new PAGEXMLExtractor();

//        ClassLoader classLoader = getClass().getClassLoader();
//        String filePath = classLoader.getResource("/extractor_config.properties").getFile();
        File configFile = new File("src/test/resources/extractor_config.properties");

        extractor_config = new PAGEXMLExtractor(configFile);
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testExpandAbbreviation()
    {
        String customTagValue = "readingOrder {index:17;} abbrev {offset:25; length:6;expansion:Hooch-Mogende;}";
        String textOriginal = "#reert synde hebben haer Ho:Mo: hun daermede";
        String textExpanded = "#reert synde hebben haer Hooch-Mogende hun daermede";

        String result = extractor_simple.expandAbbreviations(textOriginal, customTagValue);
        assertEquals(textExpanded, result);

        result = extractor_simple.getTextFromNode(textOriginal, customTagValue);
        assertEquals(textOriginal, result);
        result = extractor_simple.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, result);
        result = extractor_simple.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, result);

        result = extractor_config.getTextFromNode(textOriginal, customTagValue);
        assertEquals(textExpanded, result);
        result = extractor_config.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, result);
        result = extractor_config.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, result);
    }

    @Test
    public void testDoubleExpandAbbreviation()
    {
        String customTagValue = "readingOrder {index:17;} abbrev {offset:25; length:6;expansion:Hooch-Mogende;} abbrev {offset:32; length:6;expansion:Hooch-Mogende;}";
        String textOriginal = "#reert synde hebben haer Ho:Mo: Ho:Mo: hun daermede";
        String textExpanded = "#reert synde hebben haer Hooch-Mogende Hooch-Mogende hun daermede";

        String expanded = extractor_simple.getTextFromNode(textOriginal, customTagValue);
        assertEquals(textOriginal, expanded);
        expanded = extractor_simple.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, expanded);
        expanded = extractor_simple.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, expanded);

        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue);
        assertEquals(textExpanded, expanded);
        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, expanded);
        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, expanded);
    }

    @Test
    public void testMissingExpansion()
    {
        String customTagValue = "readingOrder {index:17;} abbrev {offset:25; length:6;expansion:Hooch-Mogende;} abbrev {offset:32; length:6;}";
        String textOriginal = "#reert synde hebben haer Ho:Mo: Ho:Mo: hun daermede";
        String textExpanded = "#reert synde hebben haer Hooch-Mogende Ho:Mo: hun daermede";

        String parsed = extractor_simple.getTextFromNode(textOriginal, customTagValue);
        assertEquals(textOriginal, parsed);
        parsed = extractor_simple.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, parsed);
        parsed = extractor_simple.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, parsed);

        parsed = extractor_config.getTextFromNode(textOriginal, customTagValue);
        assertEquals(textExpanded, parsed);
        parsed = extractor_config.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, parsed);
        parsed = extractor_config.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, parsed);
    }

    public void testExtractingAbbreviations()
    {
        String customTagValue = "readingOrder {index:17;} abbrev {offset:25; length:6;expansion:Hooch-Mogende;} abbrev {offset:32; length:6;}";
        String textOriginal = "#reert synde hebben haer Ho:Mo: Ho:Mo: hun daermede";

        IDictionary abbrevations = extractor_simple.extractAbbrevations(textOriginal, customTagValue);

        assertEquals(1, abbrevations.getEntries().size());
        assertTrue(abbrevations.containsKey("Ho:Mo:"));
        assertTrue(abbrevations.getEntry("Ho:Mo:").containsKey("Hooch-Mogende"));
    }

}
