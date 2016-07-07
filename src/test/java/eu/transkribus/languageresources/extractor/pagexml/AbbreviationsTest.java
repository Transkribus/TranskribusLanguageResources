/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.transkribus.languageresources.extractor.pagexml;

import java.io.File;
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
public class AbbreviationsTest
{

    private final PAGEXMLExtractor extractor_simple;
    private final PAGEXMLExtractor extractor_config;

    public AbbreviationsTest()
    {
        extractor_simple = new PAGEXMLExtractor();
        
        ClassLoader classLoader = getClass().getClassLoader();
        File configFile = new File(classLoader.getResource("abbreviations.properties").getFile());
        
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
        
        String expanded = extractor_simple.expandAbbreviations(textOriginal, customTagValue);
        assertEquals(textExpanded, expanded);
        
        expanded = extractor_simple.getTextFromNode(textOriginal, customTagValue);
        assertEquals(textOriginal, expanded);
        expanded = extractor_simple.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, expanded);
        expanded = extractor_simple.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, expanded);
        
        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue);
        assertEquals(textOriginal, expanded);
        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, expanded);
        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, expanded);
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
        assertEquals(textOriginal, expanded);
        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, expanded);
        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, expanded);
    }
    
    @Test
    public void testmissingExpansion()
    {
        String customTagValue = "readingOrder {index:17;} abbrev {offset:25; length:6;expansion:Hooch-Mogende;} abbrev {offset:32; length:6;}";
        String textOriginal = "#reert synde hebben haer Ho:Mo: Ho:Mo: hun daermede";
        String textExpanded = "#reert synde hebben haer Hooch-Mogende Ho:Mo: hun daermede";
        
        String expanded = extractor_simple.getTextFromNode(textOriginal, customTagValue);
        assertEquals(textOriginal, expanded);
        expanded = extractor_simple.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, expanded);
        expanded = extractor_simple.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, expanded);
        
        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue);
        assertEquals(textOriginal, expanded);
        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue, "expand");
        assertEquals(textExpanded, expanded);
        expanded = extractor_config.getTextFromNode(textOriginal, customTagValue, "keep");
        assertEquals(textOriginal, expanded);
    }
}
